package com.example.redrestaurantapp.ServiceLayer;

import android.os.Build;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.example.redrestaurantapp.Interfaces.GetCollectionCallback;
import com.example.redrestaurantapp.Interfaces.GetDocumentCallback;
import com.example.redrestaurantapp.Utils.ThreadPoolManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FireStore {
    private final String TAG = "FireStore";
    private final FirebaseFirestore mFirebaseFirestore;
    private final ThreadPoolManager mThreadPoolManager;

    public FireStore() {
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mThreadPoolManager = ThreadPoolManager.getInstance();
    }

    public <T> void insertOne(String collectionPath, String document, T object, com.example.redrestaurantapp.Interfaces.OnCompleteListener<Void> onSuccessListener){
        mThreadPoolManager.submitTask(new Runnable() {
            @Override
            public void run() {
                Field[] fields = object.getClass().getDeclaredFields();

                Map<String, Object> obj = new HashMap<>();
                for(Field field : fields){
                    try{
                        field.setAccessible(true);
                        obj.put(field.getName(), field.get(object));
                    }catch (Exception ex){
                        Log.d(TAG, "insertOne Error: " + ex.getLocalizedMessage());
                    }
                }

                mFirebaseFirestore.collection(collectionPath).document(document)
                        .set(obj, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                onSuccessListener.onSuccess(unused);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                onSuccessListener.onFailure(e);
                            }
                        });
            }
        });
    }

    public <T> void batchInsert(String collection, String documentPrefix, List<T> objects){
        if(objects.isEmpty()) return;

        mThreadPoolManager.submitTask(new Runnable() {
            @Override
            public void run() {
                int i = 1;
                WriteBatch batch = mFirebaseFirestore.batch();
                for(T object : objects){
                    Field[] fields = object.getClass().getDeclaredFields();
                    Map<String, Object> obj = new HashMap<>();
                    for(Field field : fields){
                        try {
                            field.setAccessible(true);
                            obj.put(field.getName(), field.get(object));
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    DocumentReference docRef = mFirebaseFirestore.collection(collection).document( documentPrefix + "" + i);
                    batch.set(docRef, obj);
                    i++;
                }

                batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "batch write successful.");
                        }else{
                            Log.d(TAG, "batch write failed ", task.getException());
                        }
                    }
                });
            }
        });
    }

    public <T> void getDocument(String collection, String document, Class<T> model, GetDocumentCallback<T> callback) {
        DocumentReference docRef = mFirebaseFirestore.collection(collection).document(document);

        mThreadPoolManager.submitTask(new Runnable() {
            @Override
            public void run() {
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();

                            if(doc.exists()){
                                try {
                                    T modelInstance = model.getDeclaredConstructor().newInstance();
                                    Field[] fields = model.getDeclaredFields();

                                    Map<String, Object> map = doc.getData();
                                    if(map == null){
                                        callback.onSuccess(modelInstance);
                                        return;
                                    }

                                    for(String key : map.keySet()){
                                        for(Field field : fields){
                                            if(field.getName().equals(key)){
                                                field.setAccessible(true);
                                                field.set(modelInstance, map.get(key));

                                                break;
                                            }
                                        }
                                    }

                                    callback.onSuccess(modelInstance);
                                }catch (Exception ex){
                                    callback.onFailure(new Exception("Reflection failure : ", ex));
                                }
                            }else{
                                callback.onFailure(new Exception("No such document."));
                            }
                        }else{
                            callback.onFailure(new Exception("Get failed with ", task.getException()));
                        }
                    }
                });
            }
        });
    }

    public <T> void getCollection(String collection, Pair<String, Object> whereEqualsTo, Class<T> model, GetCollectionCallback<T> callback){
        Query query = mFirebaseFirestore.collection(collection).whereEqualTo(whereEqualsTo.first, whereEqualsTo.second);

        mThreadPoolManager.submitTask(new Runnable() {
            @Override
            public void run() {
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            try{
                                List<T> resultList = new ArrayList<>();
                                for(QueryDocumentSnapshot document : task.getResult()){
                                    T modelInstance = model.getDeclaredConstructor().newInstance();
                                    Field[] fields = model.getDeclaredFields();
                                    Map<String, Object> map = document.getData();

                                    for(String key : map.keySet()){
                                        for(Field field : fields){
                                            if(field.getName().equals(key)){
                                                if(field.getGenericType() instanceof ParameterizedType){
                                                    constructParameterizedFieldValue(modelInstance, field, map.get(key));

                                                    break;
                                                }

                                                if(!isPrimitive(field) && canCastToHashmap(map.get(key))){
                                                    Log.d(TAG, field.getName());
                                                    constructCustomTypedField(modelInstance, field, (HashMap<String, Object>) map.get(key));

                                                    break;
                                                }

                                                field.setAccessible(true);
                                                field.set(modelInstance, map.get(key));

                                                break;
                                            }
                                        }
                                    }

                                    resultList.add(modelInstance);
                                }
                                callback.onSuccess(resultList);
                            }catch (Exception ex){
                                ex.printStackTrace();
                                callback.onFailure(ex);
                            }
                        }else{
                            callback.onFailure(new Exception("Get failed with : ", task.getException()));
                        }
                    }
                });
            }
        });
    }

    public <T> void getCollection(String collection, String orderBy, Pair<String, Object> whereEqualsTo, Class<T> model, GetCollectionCallback<T> callback){
        Query query = mFirebaseFirestore.collection(collection).whereEqualTo(whereEqualsTo.first, whereEqualsTo.second).orderBy(orderBy);

        mThreadPoolManager.submitTask(new Runnable() {
            @Override
            public void run() {
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            try{
                                List<T> resultList = new ArrayList<>();
                                for(QueryDocumentSnapshot document : task.getResult()){
                                    T modelInstance = model.getDeclaredConstructor().newInstance();
                                    Field[] fields = model.getDeclaredFields();
                                    Map<String, Object> map = document.getData();

                                    for(String key : map.keySet()){
                                        for(Field field : fields){
                                            if(field.getName().equals(key)){
                                                if(field.getGenericType() instanceof ParameterizedType){
                                                    constructParameterizedFieldValue(modelInstance, field, map.get(key));

                                                    break;
                                                }

                                                if(!isPrimitive(field) && canCastToHashmap(map.get(key))){
                                                    Log.d(TAG, field.getName());
                                                    constructCustomTypedField(modelInstance, field, (HashMap<String, Object>) map.get(key));

                                                    break;
                                                }

                                                field.setAccessible(true);
                                                field.set(modelInstance, map.get(key));

                                                break;
                                            }
                                        }
                                    }

                                    resultList.add(modelInstance);
                                }
                                callback.onSuccess(resultList);
                            }catch (Exception ex){
                                ex.printStackTrace();
                                callback.onFailure(ex);
                            }
                        }else{
                            callback.onFailure(new Exception("Get failed with : ", task.getException()));
                        }
                    }
                });
            }
        });
    }

    public <T> void getCollection(String collection, Class<T> model, GetCollectionCallback<T> callback) {
        mThreadPoolManager.submitTask(new Runnable() {
            @Override
            public void run() {
                CollectionReference collectionRef = mFirebaseFirestore.collection(collection);
                collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            try {
                                List<T> resultList = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    T modelInstance = model.getDeclaredConstructor().newInstance();
                                    Field[] fields = model.getDeclaredFields();
                                    Map<String, Object> map = document.getData();

                                    for(String key : map.keySet()){
                                        for(Field field : fields){
                                            if(field.getName().equals(key)){
                                                field.setAccessible(true);
                                                field.set(modelInstance, map.get(key));

                                                break;
                                            }
                                        }
                                    }

                                    resultList.add(modelInstance);
                                }

                                callback.onSuccess(resultList);
                            }catch (Exception ex){
                                callback.onFailure(ex);
                            }
                        }else{
                            callback.onFailure(new Exception("Get failed with ", task.getException()));
                        }
                    }
                });
            }
        });
    }

    public <T> void getCollection(String collection, int limit, String orderBy, Pair<String, Object> whereEqualTo, Class<T> model,  GetCollectionCallback<T> callback) {
        Query query = mFirebaseFirestore.collection(collection).orderBy(orderBy).whereEqualTo(whereEqualTo.first, whereEqualTo.second).limitToLast(limit);

        mThreadPoolManager.submitTask(new Runnable() {
            @Override
            public void run() {
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            try {
                                List<T> resultList = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    T modelInstance = model.getDeclaredConstructor().newInstance();
                                    Field[] fields = model.getDeclaredFields();
                                    Map<String, Object> map = document.getData();

                                    for(String key : map.keySet()){
                                        for(Field field : fields){
                                            if(field.getName().equals(key)){
                                                if(field.getGenericType() instanceof ParameterizedType){
                                                    constructParameterizedFieldValue(modelInstance, field, map.get(key));

                                                    break;
                                                }

                                                if(!isPrimitive(field) && canCastToHashmap(map.get(key))){
                                                    Log.d(TAG, field.getName());
                                                    constructCustomTypedField(modelInstance, field, (HashMap<String, Object>) map.get(key));

                                                    break;
                                                }

                                                field.setAccessible(true);
                                                field.set(modelInstance, map.get(key));
                                            }
                                        }
                                    }

                                    resultList.add(modelInstance);
                                }

                                callback.onSuccess(resultList);
                            }catch (Exception ex){
                                callback.onFailure(ex);
                            }
                        }else{
                            callback.onFailure(new Exception("Get failed with ", task.getException()));
                        }
                    }
                });
            }
        });
    }

    private <T> void constructParameterizedFieldValue(T parentObject, Field field, Object value) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
        Type[] actualTypeArgs = parameterizedType.getActualTypeArguments();
        Class<?> typeArgument = (Class<?>)actualTypeArgs[0];

        List<HashMap<String, Object>> mapList = (List<HashMap<String, Object>>) value;
        List<Object> list = new ArrayList<>();

        for(HashMap<String, Object> map : mapList){
            Object instance = typeArgument.getDeclaredConstructor().newInstance();
            Field[] fields = instance.getClass().getDeclaredFields();

            for(String _key : map.keySet()){
                for(Field _field : fields){
                    if(_field.getName().equals(_key)){
                        if(!_field.getType().isPrimitive()){
                            constructCustomTypedField(instance, _field, (HashMap<String, Object>) map.get(_key));
                            continue;
                        }

                        _field.setAccessible(true);
                        _field.set(instance, map.get(_key));
                    }
                }
            }

            list.add(instance);
            Log.d(TAG, "Order object [final] -> " + instance.toString());
        }

        field.setAccessible(true);
        field.set(parentObject, list);
    }

    private <T> void constructCustomTypedField(T parentObject, Field field, HashMap<String, Object> map) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Object instance = field.getType().getDeclaredConstructor().newInstance();

        Field[] fields = instance.getClass().getDeclaredFields();
        for(String key : map.keySet()){
            for(Field _field : fields){
                if(_field.getName().equals(key)){
                    _field.setAccessible(true);
                    _field.set(instance, map.get(key));
                }
            }
        }

        field.setAccessible(true);
        field.set(parentObject, instance);

        Log.d(TAG, "Product object type -> " + instance.getClass().getTypeName());
        Log.d(TAG, "Product object -> " + instance.toString());
    }

    private boolean isPrimitive(Field field){
        return field.getType() == boolean.class ||
                field.getType() == char.class ||
                field.getType() == byte.class ||
                field.getType() == short.class ||
                field.getType() == int.class ||
                field.getType() == long.class ||
                field.getType() == float.class ||
                field.getType() == double.class ||
                field.getType() == String.class;
    }
    
    private <T> boolean canCastToHashmap(T object){
        try{
            HashMap<String, Object> obj = (HashMap<String, Object>) object;
            return true;
        }catch (Exception ex){
            return false;
        }
    }
}

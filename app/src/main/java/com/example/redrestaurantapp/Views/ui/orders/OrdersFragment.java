package com.example.redrestaurantapp.Views.ui.orders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redrestaurantapp.Adapters.OrdersAdapter;
import com.example.redrestaurantapp.Controllers.OrderController;
import com.example.redrestaurantapp.R;
import com.example.redrestaurantapp.Utils.ThreadPoolManager;
import com.example.redrestaurantapp.Views.ViewOrderActivity;
import com.example.redrestaurantapp.databinding.FragmentOrdersBinding;
import com.facebook.shimmer.ShimmerFrameLayout;

public class OrdersFragment extends Fragment {
    private final String TAG = "OrdersFragment";

    private FragmentOrdersBinding binding;

    private ShimmerFrameLayout mShimmeringOverlay;

    private final OrderController mOrderController;
    private final ThreadPoolManager mThreadPoolManager;
    private RecyclerView mOrderRecycler;
    private OrdersAdapter mOrderAdapter;

    public OrdersFragment() {
        mOrderController = new OrderController();
        mThreadPoolManager = ThreadPoolManager.getInstance();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrdersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mShimmeringOverlay = root.findViewById(R.id.ordersShimmeringLayout);

        mOrderRecycler = root.findViewById(R.id.ordersRecycler);

        populateOrderRecycler();

        return root;
    }

    private void populateOrderRecycler() {
        setShimmering(true);

        mThreadPoolManager.submitTask(new Runnable() {
            @Override
            public void run() {
                while(mOrderController.isLoading());
                Log.d(TAG, "finished loading orders");

                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mOrderAdapter = new OrdersAdapter(getActivity(), mOrderController.getOrders(), OrdersFragment.this::onItemClick);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        mOrderRecycler.setLayoutManager(layoutManager);
                        mOrderRecycler.setAdapter(mOrderAdapter);

                        setShimmering(false);
                    }
                });
            }
        });
    }

    private void setShimmering(boolean state){
        try{
            if(state){
                mShimmeringOverlay.setVisibility(View.VISIBLE);
                mShimmeringOverlay.startShimmer();
                return;
            }
            mShimmeringOverlay.setVisibility(View.GONE);
            mShimmeringOverlay.stopShimmer();
        }catch (Exception ex){
            Log.d(TAG, ex.getLocalizedMessage());
        }
    }

    private void onItemClick(int idx) {
        Log.d(TAG, "" + idx);
        Intent viewOrderActivity = new Intent(requireActivity(), ViewOrderActivity.class);
        viewOrderActivity.putExtra("orderIdx", idx);
        startActivity(viewOrderActivity);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
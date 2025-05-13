package com.kravitzsurf.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kravitzsurf.R;
import com.kravitzsurf.models.SurfClass;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {
    
    private Context context;
    private List<SurfClass> classList;
    private OnClassClickListener listener;
    
    public interface OnClassClickListener {
        void onClassClick(SurfClass surfClass);
        void onEnrollClick(SurfClass surfClass);
    }
    
    public ClassAdapter(Context context, List<SurfClass> classList, OnClassClickListener listener) {
        this.context = context;
        this.classList = classList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        SurfClass surfClass = classList.get(position);
        
        holder.titleTextView.setText(surfClass.getTitle());
        holder.instructorTextView.setText("Instructor: " + surfClass.getInstructor());
        
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
        String dateStr = sdf.format(new Date(surfClass.getDateTime()));
        holder.dateTimeTextView.setText(dateStr);
        
        holder.durationTextView.setText(surfClass.getDuration() + " min");
        holder.priceTextView.setText("$" + surfClass.getPrice());
        
        holder.itemView.setOnClickListener(v -> listener.onClassClick(surfClass));
        holder.enrollButton.setOnClickListener(v -> listener.onEnrollClick(surfClass));
    }
    
    @Override
    public int getItemCount() {
        return classList.size();
    }
    
    static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, instructorTextView, dateTimeTextView;
        TextView durationTextView, priceTextView;
        Button enrollButton;
        
        ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            instructorTextView = itemView.findViewById(R.id.instructorTextView);
            dateTimeTextView = itemView.findViewById(R.id.dateTimeTextView);
            durationTextView = itemView.findViewById(R.id.durationTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            enrollButton = itemView.findViewById(R.id.enrollButton);
        }
    }
}

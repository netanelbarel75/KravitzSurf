package com.kravitzsurf.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kravitzsurf.R;
import com.kravitzsurf.models.SurfClass;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EnrolledClassAdapter extends RecyclerView.Adapter<EnrolledClassAdapter.ViewHolder> {
    
    private final Context context;
    private final List<SurfClass> classList;
    private final OnClassClickListener listener;
    
    public interface OnClassClickListener {
        void onClassClick(SurfClass surfClass);
    }
    
    public EnrolledClassAdapter(Context context, List<SurfClass> classList, OnClassClickListener listener) {
        this.context = context;
        this.classList = classList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_enrolled_class, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SurfClass surfClass = classList.get(position);
        
        holder.titleTextView.setText(surfClass.getTitle());
        holder.instructorTextView.setText(context.getString(R.string.instructor_format, surfClass.getInstructor()));
        
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault());
        String dateStr = sdf.format(new Date(surfClass.getDateTime()));
        holder.dateTimeTextView.setText(dateStr);
        
        holder.typeTextView.setText(getTypeDisplayName(surfClass.getType()));
        
        holder.itemView.setOnClickListener(v -> listener.onClassClick(surfClass));
    }
    
    @Override
    public int getItemCount() {
        return classList.size();
    }
    
    private String getTypeDisplayName(String type) {
        switch (type) {
            case "group":
                return "Group Class";
            case "private":
                return "Private Class";
            case "parent_child":
                return "Parent & Child Class";
            default:
                return type;
        }
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, instructorTextView, dateTimeTextView, typeTextView;
        
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            instructorTextView = itemView.findViewById(R.id.instructorTextView);
            dateTimeTextView = itemView.findViewById(R.id.dateTimeTextView);
            typeTextView = itemView.findViewById(R.id.typeTextView);
        }
    }
}

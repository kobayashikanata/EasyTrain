package com.per.epx.easytrain.adapters;

import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.List;

public class Selector {
    private SparseBooleanArray selectedList = new SparseBooleanArray();
    private SparseBooleanArray selection = new SparseBooleanArray();

    public void reset(int newSize){
        setupSize(newSize);
    }

    public void setupSize(int newSize){
        clear();
        for(int i = 0; i < newSize; i++){
            unSelect(i);
        }
    }

    public void select(int position){
        setSelect(position, true);
    }

    public void unSelect(int position){
        setSelect(position, false);
    }

    public void setSelect(int position, boolean selected){
        updateSelectionAt(position, selected);
    }

    public void toggle(int position){
        setSelect(position, !selection.get(position));
    }

    public void selectAll(){
        for(int i = 0; i < selection.size(); i++){
            int position = selection.keyAt(i);
            selection.put(position, true);
            selectedList.put(position, true);
        }
    }

    public void setAll(boolean checked){
        if(checked){
            selectAll();
        }else{
            unSelectAll();
        }
    }

    public void unSelectAll(){
        selectedList.clear();
        for(int i = 0; i < selection.size(); i++){
            selection.put(selection.keyAt(i), false);
        }
    }

    public void toggleAll(){
        for(int i = 0; i < selection.size(); i++){
            toggle(i);
        }
    }

    public void remove(int position){
        selection.delete(position);
        selectedList.delete(position);
    }

    public void clear(){
        this.selection.clear();
        this.selectedList.clear();
    }

    public boolean isSelected(int position){
        return selection.get(position);
    }

    public int getSelectedCount() {
        return selectedList.size();
    }

    public List<Integer> copySelectedPositionList(){
        List<Integer> positionList = new ArrayList<>();
        for(int i = 0; i < selectedList.size(); i++){
            positionList.add(selectedList.keyAt(i));
        }
        return positionList;
    }

    private void updateSelectionAt(int position, boolean selected){
        selection.put(position, selected);
        if(selected){
            selectedList.put(position, true);
        }else{
            selectedList.delete(position);
        }
    }
}

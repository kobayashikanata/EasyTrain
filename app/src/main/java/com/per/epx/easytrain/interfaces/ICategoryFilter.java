package com.per.epx.easytrain.interfaces;

import java.util.List;

public interface ICategoryFilter<SubCategory, DataType> {
    String getName();
    boolean setSubType(SubCategory typeValue);
    SubCategory getSubType();
    List<DataType> filter(List<DataType> source);
}

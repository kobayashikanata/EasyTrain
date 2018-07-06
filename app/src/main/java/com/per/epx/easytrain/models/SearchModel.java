package com.per.epx.easytrain.models;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.os.Parcel;
import android.os.Parcelable;

import com.per.epx.easytrain.models.req.SolutionAsk;

import java.io.Serializable;

public class SearchModel implements Serializable, Parcelable{
    public final ObservableField<Depot> from = new ObservableField<>();
    public final ObservableField<Depot> to = new ObservableField<>();
    public final ObservableField<Integer> type = new ObservableField<>(SolutionAsk.CHEAPEST);
    public final ObservableField<Long> dateMs = new ObservableField<>(System.currentTimeMillis());
    //public final ObservableField<Interval<Integer>> interval = new ObservableField<>(new Interval<>(0, 1440));
    public final ObservableList<String> transitCodes = new ObservableArrayList<>();

    public SearchModel(){}

    @SuppressWarnings({"unchecked", "WeakerAccess"})
    protected SearchModel(Parcel in) {
        from.set(((ObservableField<Depot>)in.readSerializable()).get());
        to.set(((ObservableField<Depot>)in.readSerializable()).get());
        type.set(((ObservableField<Integer>)in.readSerializable()).get());
        dateMs.set(((ObservableField<Long>)in.readSerializable()).get());
        //interval.set(((ObservableField<Interval<Integer>>)in.readSerializable()).get());
        in.readStringList(transitCodes);
    }

    public static final Creator<SearchModel> CREATOR = new Creator<SearchModel>() {
        @Override
        public SearchModel createFromParcel(Parcel in) {
            return new SearchModel(in);
        }

        @Override
        public SearchModel[] newArray(int size) {
            return new SearchModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(from);
        dest.writeSerializable(to);
        dest.writeSerializable(type);
        dest.writeSerializable(dateMs);
        //dest.writeSerializable(interval);
        dest.writeStringList(transitCodes);
    }
}

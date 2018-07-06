package com.per.epx.easytrain.viewmodels;

import android.support.annotation.IntDef;

import com.per.epx.easytrain.interfaces.IViewModel;
import com.per.epx.easytrain.models.AcceptorGroup;
import com.per.epx.easytrain.models.Interval;
import com.per.epx.easytrain.models.ListAcceptable;
import com.per.epx.easytrain.models.sln.LineRoute;
import com.per.epx.easytrain.models.sln.Segment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RouteDataViewModel implements IViewModel{

    public static final int SORT_DRIVE = 0;
    public static final int SORT_ARRIVE = 1;
    public static final int SORT_DURATION = 2;
    public static final int SORT_PAY = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SORT_DRIVE, SORT_ARRIVE, SORT_DURATION, SORT_PAY})
    public @interface SortType{}


    private final DataHolder dataHolder = new DataHolder();
    private TimeRangeAcceptor timeRangeAcceptor = new TimeRangeAcceptor();
    private Comparator<LineRoute> routeComparator;
    private boolean isOneSegment = false;
    private AcceptorGroup acceptorGroup = new AcceptorGroup();
    private SearchViewModel searchVm;

    public void initialize(List<LineRoute> plans){
        for(LineRoute plan : plans){
            plan.getBeginning().idToCodeIfNull();
            plan.getTerminal().idToCodeIfNull();
            for(Segment segment : plan.getRunLines()){
                segment.getBeginning().idToCodeIfNull();
                segment.getTerminal().idToCodeIfNull();
            }
        }
        //Clean before
        dataHolder.acceptors().removeAcceptor(acceptorGroup.beginningDepot);
        dataHolder.acceptors().removeAcceptor(acceptorGroup.terminalDepot);
        dataHolder.acceptors().removeAcceptor(acceptorGroup.firstSeatType);
        dataHolder.acceptors().removeAcceptor(acceptorGroup.inStationTransfer);
        dataHolder.acceptors().removeAcceptor(acceptorGroup.oneSegment);
        dataHolder.acceptors().removeAcceptor(timeRangeAcceptor);
        //Re-initialize
        acceptorGroup = new AcceptorGroup();
        timeRangeAcceptor.initialize();
        //Re-add acceptors
        dataHolder.acceptors().addAcceptor(timeRangeAcceptor);
        dataHolder.acceptors().addAcceptor(acceptorGroup.oneSegment);
        dataHolder.replace(plans);
        //Update adapter
        //dataHolder.beginningDepot.acceptAll();
        //dataHolder.terminalDepot.acceptAll();
//        for (LineRoute route : plans){
//            String beginningStopName = route.getBeginning().name;
//            String terminalStopName = route.getTerminal().name;
//                if(!dataHolder.beginningDepot.getInAccept().contains(beginningStopName)){
//                    dataHolder.beginningDepot.getInAccept().add(beginningStopName);
//                }
//                if(!dataHolder.terminalDepot.getInAccept().contains(terminalStopName)){
//                    dataHolder.terminalDepot.getInAccept().add(terminalStopName);
//                }
//        }
    }

    @Override
    public void cleanUp() {
    }

    public List<LineRoute> getData(){
        return dataHolder.data();
    }

    private TimeRangeAcceptor getTimeRange() {
        return timeRangeAcceptor;
    }

    public Interval<Integer> getDriveMinutesRange(){
        return getTimeRange().driveMinutesRange;
    }

    public Interval<Integer> getArriveMinutesRange(){
        return getTimeRange().arriveMinutesRange;
    }

    public Interval<Integer> getTransferMinutesRange(){
        return getTimeRange().transferMinutesRange;
    }

    public void updateTimeRange(Interval<Integer> driveMinutes,
                                Interval<Integer> arriveMinutes,
                                Interval<Integer> transferMinutes){
        this.getDriveMinutesRange().from.set(driveMinutes.from.get());
        this.getDriveMinutesRange().to.set(driveMinutes.to.get());
        this.getArriveMinutesRange().from.set(arriveMinutes.from.get());
        this.getArriveMinutesRange().to.set(arriveMinutes.to.get());
        this.getTransferMinutesRange().from.set(transferMinutes.from.get());
        this.getTransferMinutesRange().to.set(transferMinutes.to.get());
    }

    public void setOneSegment(boolean oneSegment) {
        isOneSegment = oneSegment;
        acceptorGroup.oneSegment.setAcceptable(oneSegment);
        updateAccept();
    }

    public boolean isOneSegment() {
        return isOneSegment;
    }


    public void updateAccept(){
        dataHolder.notifyAccept();
        sort();
    }

    public void setComparator(Comparator<LineRoute> comparator) {
        this.routeComparator = comparator;
    }

    public void setComparatorAndSort(Comparator<LineRoute> comparator) {
        this.routeComparator = comparator;
        sort();
    }

    public void sort(){
        if(routeComparator != null){
            Collections.sort(getData(), routeComparator);
        }
    }

    public void updateComparator(@SortType int sortType){
        setComparator(createRouteComparator(sortType));
    }

    public void updateComparatorAndSort(@SortType int sortType){
        setComparatorAndSort(createRouteComparator(sortType));
    }

    public Comparator<LineRoute> createRouteComparator(@SortType int sortType){
        switch (sortType){
            case SORT_DRIVE:
                return new Comparator<LineRoute>() {
                    @Override
                    public int compare(LineRoute o1, LineRoute o2) {
                        return o1.getBeginning().getDriveTime() > o2.getBeginning().getDriveTime() ? 1 : -1;
                    }
                };
            case SORT_ARRIVE:
                return new Comparator<LineRoute>() {
                    @Override
                    public int compare(LineRoute o1, LineRoute o2) {
                        return o1.getTerminal().getArrivalTime() > o2.getTerminal().getArrivalTime() ? 1 : -1;
                    }
                };
            case SORT_DURATION:
                return new Comparator<LineRoute>() {
                    @Override
                    public int compare(LineRoute o1, LineRoute o2) {
                        long runTime1 = o1.getTerminal().getArrivalTime() - o1.getBeginning().getDriveTime();
                        long runTime2 = o2.getTerminal().getArrivalTime() - o2.getBeginning().getDriveTime();
                        return runTime1 > runTime2 ? 1 : -1;
                    }
                };
            case SORT_PAY:
                return new Comparator<LineRoute>() {
                    @Override
                    public int compare(LineRoute o1, LineRoute o2) {
                        return o1.getPayment() > o2.getPayment() ? 1 : -1;
                    }
                };
            default:return null;
        }
    }

    public static class TimeRangeAcceptor implements ListAcceptable.IAcceptable<LineRoute>{
        private final Interval<Integer> driveMinutesRange;
        private final Interval<Integer> arriveMinutesRange;
        private final Interval<Integer> transferMinutesRange;
        private static final int maxMinutes = 24*60;

        public void initialize(){
            driveMinutesRange.from.set(0);
            driveMinutesRange.to.set(maxMinutes);
            arriveMinutesRange.from.set(0);
            arriveMinutesRange.to.set(maxMinutes);
            transferMinutesRange.from.set(0);
            transferMinutesRange.to.set(maxMinutes);
        }

        public TimeRangeAcceptor(){
            driveMinutesRange = new Interval<>(0, maxMinutes);
            arriveMinutesRange = new Interval<>(0, maxMinutes);
            transferMinutesRange = new Interval<>(0, maxMinutes);
        }

        @Override
        public boolean accept(LineRoute route){
            if(route.getRunLines().size() < 1){
                return true;
            }
            Calendar drive = Calendar.getInstance();
            Calendar arrive = Calendar.getInstance();
            drive.setTimeInMillis(route.getBeginning().getDriveTime());
            arrive.setTimeInMillis(route.getTerminal().getArrivalTime());
            int driveMinutes = drive.get(Calendar.HOUR_OF_DAY)*60 + drive.get(Calendar.MINUTE);
            int arriveMinutes = arrive.get(Calendar.HOUR_OF_DAY)*60 + arrive.get(Calendar.MINUTE);
            if(!in(driveMinutesRange, driveMinutes)
                    || !in(arriveMinutesRange, arriveMinutes)){
                return false;
            }
            List<Segment> lines = route.getRunLines();
            long lastArriveTime = lines.get(0).getTerminal().getArrivalTime();
            int minFrom =  transferMinutesRange.from.get();
            int maxTo =  transferMinutesRange.to.get();
            for(int i = 1; i < lines.size(); i++){
                long differ = (lines.get(i).getBeginning().getDriveTime() - lastArriveTime) / (1000*60);
                if(differ <= 0) continue;
                if((differ < minFrom) || (differ > maxTo && maxTo < maxMinutes)){
                    return false;
                }
                lastArriveTime = lines.get(i).getTerminal().getArrivalTime();
            }
            if(transferMinutesRange.to.get() < maxMinutes){
            }
            return true;
        }

        private boolean in(Interval<Integer> interval, int value){
            return interval.from.get() <= value && value <= interval.to.get();
        }
    }

    public static class DataHolder {
        private final ListAcceptable<LineRoute> acceptorList;

        public DataHolder(){
            this(new ArrayList<LineRoute>());
        }

        public DataHolder(List<LineRoute> data){
            this.acceptorList = new ListAcceptable<>(data);
            this.acceptors().addAcceptor(new AcceptorGroup().beginningDepot);
            this.acceptors().addAcceptor(new AcceptorGroup().terminalDepot);
        }

        public List<LineRoute> data(){
            return acceptorList.getAccepted();
        }

        public ListAcceptable<LineRoute> acceptors(){
            return this.acceptorList;
        }

        public void replace(List<LineRoute> data){
            this.acceptorList.replaceSource(data);
        }

        public void notifyAccept(){
            this.acceptorList.notifyAcceptorsChanged();
        }

    }
}

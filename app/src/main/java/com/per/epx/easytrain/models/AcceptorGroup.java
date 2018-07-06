package com.per.epx.easytrain.models;

import com.per.epx.easytrain.models.sln.LineRoute;
import com.per.epx.easytrain.models.sln.Segment;

import java.util.ArrayList;
import java.util.List;

public class AcceptorGroup {

    //Filter which its run lines is 1 that means it won't need transfer
    public final ControllableAcceptor<LineRoute> oneSegment = new ControllableAcceptor<LineRoute>() {
        @Override
        protected boolean acceptImpl(LineRoute value) {
            return value.getRunLines().size() == 1;
        }
    };

    public final ListAcceptable.IAcceptable<LineRoute> inStationTransfer = new ListAcceptable.IAcceptable<LineRoute>() {
        @Override
        public boolean accept(LineRoute value) {
            if(value.getRunLines().size() <= 1){
                return true;
            }else{
                Segment previous = value.getRunLines().get(0);
                for(int i = 1; i < value.getRunLines().size(); i++){
                    Segment current = value.getRunLines().get(i);
                    //Check stopCode
                    if(!previous.getTerminal().getCode().equals(current.getBeginning().getCode())
                            || previous.getTerminal().getId() != current.getBeginning().getId()){
                        return false;
                    }
                    //Update for next check
                    previous = current;
                }
                return true;
            }
        }
    };

    //filter which segment is in the special type
    public final AcceptInList<Integer, LineRoute> firstSeatType = new AcceptInList<Integer, LineRoute>() {
        @Override
        public boolean acceptImpl(Integer acceptValue, LineRoute value) {
            return value.getRunLines().size() == 1 && value.getRunLines().get(0).getSeatType() == acceptValue;
        }
    };

    public final AcceptInList<String, LineRoute> beginningDepot = new AcceptInList<String, LineRoute>() {
        @Override
        public boolean acceptImpl(String acceptValue, LineRoute value) {
            return acceptValue.equals(value.getBeginning().getCode());
        }
    };

    public final AcceptInList<String, LineRoute> terminalDepot = new AcceptInList<String, LineRoute>() {
        @Override
        public boolean acceptImpl(String acceptValue, LineRoute value) {
            return acceptValue.equals(value.getTerminal().getCode());
        }
    };


    public static abstract class ControllableAcceptor<T> implements ListAcceptable.IAcceptable<T>{
        private boolean acceptable = false;

        public boolean isAcceptable() {
            return acceptable;
        }

        public void setAcceptable(boolean acceptable) {
            this.acceptable = acceptable;
        }

        @Override
        public boolean accept(T value) {
            return !isAcceptable() || acceptImpl(value);
        }

        protected abstract boolean acceptImpl(T value);
    }

    //Class filter "In" values
    public static abstract class AcceptInList<AcceptValue, T> implements ListAcceptable.IAcceptable<T> {
        private final List<AcceptValue> inAccept = new ArrayList<>();

        public List<AcceptValue> getInAccept(){
            return inAccept;
        }

        public void acceptAll(){
            this.inAccept.clear();
        }

        public boolean isAllAccepted(){
            return inAccept.size() == 0;
        }

        @Override
        public boolean accept(T value) {
            if(isAllAccepted()){
                return true;
            }
            for(AcceptValue acceptValue : inAccept){
                if(acceptImpl(acceptValue, value)){
                    return true;
                }
            }
            return false;
        }

        protected abstract boolean acceptImpl(AcceptValue acceptValue, T value);
    }
}

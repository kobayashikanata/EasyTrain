package com.per.epx.easytrain.testing;

import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.per.epx.easytrain.App;
import com.per.epx.easytrain.helpers.FileUtils;
import com.per.epx.easytrain.helpers.RouteAskValidator;
import com.per.epx.easytrain.interfaces.IAction1;
import com.per.epx.easytrain.interfaces.Interceptor;
import com.per.epx.easytrain.interfaces.api.IRemoteApi;
import com.per.epx.easytrain.models.DepotLite;
import com.per.epx.easytrain.models.DepotsModel;
import com.per.epx.easytrain.models.EntireLine;
import com.per.epx.easytrain.models.Pass;
import com.per.epx.easytrain.models.req.DepotTimetableAsk;
import com.per.epx.easytrain.models.req.DepotTimetableReply;
import com.per.epx.easytrain.models.req.LineBaseReply;
import com.per.epx.easytrain.models.req.LineDetailAsk;
import com.per.epx.easytrain.models.req.LineDetailReply;
import com.per.epx.easytrain.models.req.SolutionAsk;
import com.per.epx.easytrain.models.req.SolutionReply;
import com.per.epx.easytrain.models.sln.LineRoute;
import com.per.epx.easytrain.models.sln.Segment;
import com.per.epx.easytrain.models.sln.Slice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocalApiTestImpl implements IRemoteApi {
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private final Random rText = new Random(System.currentTimeMillis());
    private final Random rInt = new Random(System.currentTimeMillis());
    private final char[] chars = new char[]{'A','B','C','D','E','F',
            'G','H','I','J','K','L','M','N',
            'O','P','Q','R','S','T','U','V',
            'W','X','Y','Z'};
    @SuppressWarnings("FieldCanBeLocal")
    private final String version = "v1.0";

    public void test(){
        IRemoteApi api = new LocalApiTestImpl();
        SolutionAsk ask = new SolutionAsk();
        ask.setFrom(new DepotLite(0, "AAA","AAA"));
        ask.setTo(new DepotLite(0,"ZZZ","ZZZ"));
        ask.setTripDate(System.currentTimeMillis());
        ask.setType(SolutionAsk.CHEAPEST);
        ask.setVersion("v1.0");
        ask.setTransitCodes(new ArrayList<String>());
        api.findRoute(-1, ask, new IAction1<SolutionReply>() {
            @Override
            public void invoke(SolutionReply reply) {
                // TODO Auto-generated method stub
                SimpleDateFormat sdf= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
                System.out.println("From >>  " + reply.getFrom());
                System.out.println("To   >>  " + reply.getTo());
                System.out.println("Msg  >>  " + reply.getMessage());
                System.out.println("Code >>  " + reply.getReplyCode());
                System.out.println("TripDate >>  " + sdf.format(reply.getTripDate()));
                System.out.println("Ver  >>  " + reply.getVersion());
                List<LineRoute> plans = reply.getPlans();
                for(LineRoute plan : plans) {
                    System.out.println("plan >>  " + plan.getPayment());
                    for(Segment segment : plan.getRunLines()) {
                        System.out.println("     >>  " + segment.getLineCode());
                        System.out.println("   ->" + segment.getBeginning().getCode());
                        System.out.println("   <-" + segment.getTerminal().getCode());
                        /*for(int i = 0; i < segment.getSlices().size(); i++) {
                            Slice sec = segment.getSlices().get(i);
                            if(i == segment.getBeginningIndex() || i == segment.getTerminalIndex()) {
                                System.out.print("     >>  " + sec.stopCode);
                                System.out.println("  |  " + sdf.format(new Date(sec.arrivalTime)));
                            }else {
                                System.out.print("     >>    " + sec.stopCode);
                                System.out.println("|  " + sdf.format(new Date(sec.arrivalTime)));
                            }
                        }*/
                        System.out.println("     ---------------------------");
                    }
                }
            }

        });
    }

    private static IRemoteApi remoteApi = null;
    public static IRemoteApi getInstance(){
        synchronized (LocalApiTestImpl.class){
            if(remoteApi == null){
                remoteApi = new LocalApiTestImpl();
            }
            return remoteApi;
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static void safeSleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean register(int key, Interceptor interceptor) {
        return false;
    }

    @Override
    public void unregister(int key, Interceptor interceptor) {

    }

    @Override
    public void unregister(Interceptor interceptor) {

    }

    @Override
    public void cleanUp() {
    }

    @Override
    public void findTimetable(int key, final DepotTimetableAsk toAsk, final IAction1<DepotTimetableReply> onReply) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                safeSleep(1000);
                long askTime = toAsk.getTimeOfYearMonthDay();
                int size = new Random(System.currentTimeMillis()).nextInt(20) + 20;

                List<Segment> segmentList = new ArrayList<>(size);
                for(int i = 0; i < size; i ++){
                    segmentList.add(makeRoute(toAsk.getDepotCode(), System.currentTimeMillis()));
                }
                DepotTimetableReply reply = new DepotTimetableReply(toAsk.getDepotId(), toAsk.getDepotCode(), toAsk.getDepotCode(), askTime, segmentList);
                String back = FileUtils.readAssertResource(App.getContext(), "test-timetable.txt");
                reply = new Gson().fromJson(back, DepotTimetableReply.class);
                onReply.invoke(reply);
            }
        });
    }

    @Override
    public void findRoute(int key, final SolutionAsk toAsk, final IAction1<SolutionReply> onReply) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                safeSleep(1000);
                SolutionReply reply = new SolutionReply();
                reply.setFrom(toAsk.getFrom());
                reply.setTo(toAsk.getTo());
                reply.setTripDate(toAsk.getTripDate());
                reply.setVersion(version);
                Segment segment = null;
                if(RouteAskValidator.DEFAULT.isAskValid(toAsk) == RouteAskValidator.PASS){
                    reply.setReplyCode(1);
                    reply.setMessage("ok");
                    String beginDepotCode = toAsk.getFrom().getCode();
                    String terminalCode = toAsk.getTo().getCode();
                    //Random generate routes result
                    List<LineRoute> routesBack = new ArrayList<>();
                    int size = getRandomInt(10, 20);
                    for(int i = 0; i < size; i++){
                        //Add result
                        LineRoute route = makeRoutePlan(beginDepotCode, terminalCode);
                        routesBack.add(route);
                    }
                    reply.setPlans(routesBack);
                    String back = FileUtils.readAssertResource(App.getContext(), "test-slns.txt");
                    reply = new Gson().fromJson(back, SolutionReply.class);
                }else{
                    reply.setReplyCode(-1);
                    reply.setMessage("invalid parameters.");
                }
                if(onReply != null){
                    onReply.invoke(reply);
                }
            }
        });
    }

    @Override
    public void findLine(int key, final LineDetailAsk toAsk, final IAction1<LineDetailReply> onReply) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                safeSleep(1000);
                if(onReply != null){
                    EntireLine line = makeEntireLine(toAsk.getLineCode(), randomString(7), toAsk.getTimeOfYearMonthDay());
                    LineDetailReply reply = new LineDetailReply();
                    reply.setLineCode(line.getCode());
                    reply.setName(line.getName());
                    Slice beginSlice = line.getPasses().get(0);
                    Slice targetSlice = line.getPasses().get(line.getPasses().size() - 1);
                    reply.setBeginning(new DepotLite(beginSlice.getId(), beginSlice.getCode(), beginSlice.getName()));
                    reply.setTerminal(new DepotLite(targetSlice.getId(), targetSlice.getCode(), targetSlice.getName()));
                    reply.setTimeOfYearMonthDay(toAsk.getTimeOfYearMonthDay());
                    reply.setPasses(new ArrayList<Pass>());
                    for(Slice pass : line.getPasses()){
                        reply.getPasses().add(new Pass(pass.getOrder(),
                                pass.getName(),
                                pass.getArrivalTime(),
                                pass.getDriveTime(),
                                pass.getStayTime()));
                    }
                    String back = FileUtils.readAssertResource(App.getContext(), "test-line-detail.txt");
                    reply = new Gson().fromJson(back, LineDetailReply.class);
                    onReply.invoke(reply);
                }
            }
        });
    }

    @Override
    public void findLinesOfKeyword(int key, final String keyword, final IAction1<List<LineBaseReply>> onReply) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                safeSleep(1000);
                List<LineBaseReply> lines = new ArrayList<LineBaseReply>();
                if(keyword == null || keyword.length() == 0){
                    onReply.invoke(lines);
                }else{
                    String startLetter = String.valueOf(keyword.charAt(0));
                    if(keyword.length() > 1){
                        startLetter += keyword.charAt(1);
                    }
                    startLetter = startLetter.toUpperCase();
                    int totalSize = getRandomInt(5, 20);
                    for(int i = 0; i < totalSize; i++){
                        String code = startLetter + randomString(4);
                        lines.add(new LineBaseReply(code, i, code));
                    }
                    String back = FileUtils.readAssertResource(App.getContext(), "test-line-of-kw.txt");
                    lines = new Gson().fromJson(back, new TypeToken<List<LineBaseReply>>(){}.getType());
                    onReply.invoke(lines);
                }
            }
        });
    }

    @Override
    public void findDepotsModel(int key, final IAction1<DepotsModel> onReply) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String values = FileUtils.readAssertResource(App.getContext(), "depots.txt");
                DepotsModel model2 = new Gson().fromJson(values, DepotsModel.class);
                safeSleep(1000);
                onReply.invoke(model2);
            }
        });
    }

    private LineRoute makeRoutePlan(String beginDepotCode, String terminalCode){
        int totalRoute = getRandomInt(3, 10);
        long baseTime = System.currentTimeMillis() + totalRoute * 360000/* / 10 */;
        String baseStopCode = beginDepotCode;
        LineRoute plan = new LineRoute();
        for(int i = 0; i < totalRoute; i++){
            Segment segment = makeRoute(baseStopCode, baseTime);
            //Setup beginning and terminal
            if(i == 0/*The fist segment as beginning*/){
                plan.setBeginning(segment.getBeginning());
            }else if(i == totalRoute - 1/*The final segment as terminal*/){
                Slice o = segment.getTerminal();
                segment.setTerminal(new Slice(segment.getCrossSize(), segment.getCrossSize(), terminalCode, terminalCode, o.getArrivalTime(), o.getDriveTime(), o.getStayTime()));
                plan.setTerminal(segment.getTerminal());
            }
            //Update base code and time for next generation
            baseTime = segment.getTerminal().getArrivalTime();
            baseStopCode = segment.getTerminal().getCode();
            //Update payment and add segment
            plan.setPayment(plan.getPayment() + segment.getPayment());
            plan.getRunLines().add(segment);
        }
        return plan;
    }

    private Segment makeRoute(String beginDepotCode, long beginningBaseTime){
        //Generate range for current segment
        int stationSize = getRandomInt(5, 20);
        //Setup basic parameters
        Segment segment = new Segment();
        segment.setLineCode("d"+getRandomInt(843, 1200));
        segment.setName(segment.getLineCode());
        segment.setPayment(getRandomInt(100, 200));
        segment.setPaymentUnit("RMB");
        segment.setCrossSize(stationSize);
        Pair<Integer, Integer> ends = getIntScope(0, stationSize-1, 3);
        /*Define time for the first section*/

        EntireLine line = makeEntireLine(segment.getLineCode(), beginDepotCode, beginningBaseTime, stationSize);
        for(int i = 0; i < line.getPasses().size(); i++){
            Slice slice = line.getPasses().get(i);
            if(i == ends.first){
                segment.setBeginning(slice);
            }else if(i == ends.second){
                segment.setTerminal(slice);
            }
        }


//        long arriveTime = beginningBaseTime - ends.first * 60 * 60 * 1000;
//        for(int k = 0; k < stationSize; k++){
//            String stopCode;
//            long runningTime;
//            //Setup slice and running-time
//            if(k == ends.first){
//                stopCode = beginningCode;
//                runningTime = beginningBaseTime - arriveTime;
//            }else{//Generate new station
//                stopCode = randomString(3);
//                runningTime = getDoubleRandomMinutes(25/*minutes*/);
//            }
//            //Random stop-time
//            long stayTime = getRandomInt(180000/*3minutes*/, 300000/*5minutes*/);
//            //Calculate the arrive time next station
//            arriveTime += (stayTime + runningTime);
//            //Add current station
//            Slice slice = new Slice(k+1, stopCode, stopCode, arriveTime, arriveTime + stayTime, stayTime);
//            //segment.getSlices().add(slice);
//            //Set beginning or terminal
//            if(k == ends.first){
//                segment.setBeginning(slice);
//            }else if(k == ends.second){
//                segment.setTerminal(slice);
//            }
//        }
        return segment;
    }


    private EntireLine makeEntireLine(String code, String beginDepotCode, long beginningBaseTime){
        return makeEntireLine(code, beginDepotCode, beginningBaseTime, 5);
    }
    private EntireLine makeEntireLine(String code, String beginDepotCode, long beginningBaseTime, int minStationSize){
        //Generate range for current segment
        int stationSize = getRandomInt(minStationSize, minStationSize+15);
        //Setup basic parameters
        EntireLine entireLine = new EntireLine();
        entireLine.setCode(code);
        entireLine.setName(code);
        entireLine.setPasses(new ArrayList<Slice>());
        /*Define time for the first section*/
        long arriveTime = beginningBaseTime;
        for(int k = 0; k < stationSize; k++){
            String stopCode;
            long runningTime;
            //Setup slice and running-time
            if(k == 0){
                stopCode = beginDepotCode;
                runningTime = beginningBaseTime - arriveTime;
            }else{//Generate new station
                stopCode = randomString(3);
                runningTime = getDoubleRandomMinutes(25/*minutes*/);
            }
            //Random stop-time
            long stayTime = getRandomInt(180000/*3minutes*/, 300000/*5minutes*/);
            //Calculate the arrive time next station
            arriveTime += (stayTime + runningTime);
            //Add current station
            Slice slice = new Slice(k+1, k+1, stopCode, stopCode, arriveTime, arriveTime + stayTime, stayTime);
            entireLine.getPasses().add(slice);
        }
        return entireLine;
    }

    @SuppressWarnings("SameParameterValue")
    private Pair<Integer, Integer> getIntScope(int min, int max, int space){
        if(min >= max || min + space > max){
            throw new UnsupportedOperationException("min must small than max, and space must be valid.");
        }
        int from = getRandomInt(min, max - space);
        int to = getRandomInt(from + space, max);
        return new Pair<>(from, to);
    }

    private int getRandomInt(int min, int max){
        if(min == max){
            return min;
        }else if(min > max){
            throw new UnsupportedOperationException("min cannot large than max.");
        }
        return rInt.nextInt(max - min) + min;
    }

    @SuppressWarnings("SameParameterValue")
    private String randomString(int size){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < size; i++){
            builder.append(chars[rText.nextInt(chars.length)]);
        }
        return builder.toString();
    }

    @SuppressWarnings("SameParameterValue")
    private int getDoubleRandomMinutes(int minutes){
        return new Random(System.currentTimeMillis()).nextInt(minutes*60*1000) + minutes*60*1000;
    }
}
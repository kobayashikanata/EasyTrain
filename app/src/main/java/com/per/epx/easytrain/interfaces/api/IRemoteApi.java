package com.per.epx.easytrain.interfaces.api;

import com.per.epx.easytrain.interfaces.IAction1;
import com.per.epx.easytrain.interfaces.Interceptor;
import com.per.epx.easytrain.models.DepotsModel;
import com.per.epx.easytrain.models.req.DepotTimetableAsk;
import com.per.epx.easytrain.models.req.DepotTimetableReply;
import com.per.epx.easytrain.models.req.LineBaseReply;
import com.per.epx.easytrain.models.req.LineDetailAsk;
import com.per.epx.easytrain.models.req.LineDetailReply;
import com.per.epx.easytrain.models.req.SolutionAsk;
import com.per.epx.easytrain.models.req.SolutionReply;

import java.util.List;

public interface IRemoteApi{
    void findTimetable(int key, DepotTimetableAsk toAsk, IAction1<DepotTimetableReply> onReply);
    void findRoute(int key, SolutionAsk toAsk, IAction1<SolutionReply> onReply);
    void findLine(int key, LineDetailAsk toAsk, IAction1<LineDetailReply> onReply);
    void findLinesOfKeyword(int key, String keyword, IAction1<List<LineBaseReply>> onReply);
    void findDepotsModel(int key, IAction1<DepotsModel> onReply);
    boolean register(int key, Interceptor interceptor);
    void unregister(int key, Interceptor interceptor);
    void unregister(Interceptor interceptor);
    void cleanUp();
}

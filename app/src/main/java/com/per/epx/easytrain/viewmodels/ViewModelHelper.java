package com.per.epx.easytrain.viewmodels;

import com.per.epx.easytrain.interfaces.IViewModel;

public class ViewModelHelper {

    public static void cleanUpVmsSafely(IViewModel... viewModels){
        for(IViewModel vm : viewModels){
            cleanUpVmSafely(vm);
        }
    }

    public static void cleanUpVmSafely(IViewModel viewModel){
        if(viewModel != null){
            try{
                viewModel.cleanUp();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}

package com.oldster.swiftmove.app.manager.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.gson.Gson;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;
import com.oldster.swiftmove.app.dao.JobDataCollectionDao;

public class JobProcessManager {


    private Context mContext;

    private JobDataCollectionDao jobDataCollectionDao;

    public JobProcessManager() {
        mContext = Contextor.getInstance().getContext();
        loadCache();
    }

    public JobDataCollectionDao getJobDataCollectionDao() {
        return jobDataCollectionDao;
    }

    public void setJobDataCollectionDao(JobDataCollectionDao jobDataCollectionDao) {
        this.jobDataCollectionDao = jobDataCollectionDao;
        saveCache();
    }

    private void loadCache() {
        SharedPreferences prefs = mContext.getSharedPreferences("JobProcess",
                Context.MODE_PRIVATE);
        String json = prefs.getString("job_process_data",null);
        if (json == null)
            return;
        jobDataCollectionDao = new Gson().fromJson(json,JobDataCollectionDao.class);
    }

    private void saveCache() {
        JobDataCollectionDao cacheDao = new JobDataCollectionDao();
        if (jobDataCollectionDao != null && jobDataCollectionDao.getData() != null)
            cacheDao.setData(jobDataCollectionDao.getData());
        String json = new Gson().toJson(cacheDao);
        SharedPreferences prefs = mContext.getSharedPreferences("JobProcess",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("job_process_data", json);
        editor.apply();
    }

    public Bundle onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("jobDataCollectionDao", jobDataCollectionDao);
        return bundle;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        jobDataCollectionDao = savedInstanceState.getParcelable("jobDataCollectionDao");
    }


}

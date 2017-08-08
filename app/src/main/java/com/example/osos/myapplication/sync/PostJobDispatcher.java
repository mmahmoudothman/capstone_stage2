package com.example.osos.myapplication.sync;


import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PostJobDispatcher extends JobService {
    private AsyncTask mAsyncTask;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        mAsyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                Context context = PostJobDispatcher.this;
                // MainActivity.doSomething(context,MainActivity.ACTION_REMINDER);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {

                jobFinished(jobParameters, false);
            }
        };
        mAsyncTask.execute();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (mAsyncTask != null) mAsyncTask.cancel(true);
        return true;
    }
}

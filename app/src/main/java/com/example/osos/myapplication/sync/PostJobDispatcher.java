package khaliliyoussef.capstoneproject.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.CalendarContract;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import khaliliyoussef.capstoneproject.activity.MainActivity;

/**
 * Created by Khalil on 8/6/2017.
 */

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

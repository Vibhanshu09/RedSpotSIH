package com.sistec.redspot;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MyMapServices extends Service {
    public MyMapServices() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

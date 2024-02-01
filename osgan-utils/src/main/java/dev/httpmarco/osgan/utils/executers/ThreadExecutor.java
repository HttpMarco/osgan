package dev.httpmarco.osgan.utils.executers;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class ThreadExecutor {

    public static final Executor EXECUTOR = Executors.newCachedThreadPool();

}

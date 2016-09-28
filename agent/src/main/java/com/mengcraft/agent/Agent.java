package com.mengcraft.agent;

import java.util.List;

/**
 * Created on 16-9-8.
 */
public interface Agent {

    void execute(Executor executor, List<String> command, boolean queued);

    void execute(Executor executor, List<String> command);

    void execute(List<String> command, boolean queued);

    void execute(List<String> command);

}

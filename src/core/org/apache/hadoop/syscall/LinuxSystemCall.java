/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.syscall;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.util.NativeCodeLoader;

public class LinuxSystemCall {

  private static Log LOG = LogFactory.getLog(LinuxSystemCall.class);

  public enum Signal {
    SIGINT(2),
    SIGQUIT(3),
    SIGILL(4),
    SIGABRT(6),
    SIGFPE(8),
    SIGKILL(9),
    SIGSEGV(11),
    SIGPIPE(13),
    SIGALRM(14),
    SIGTERM(15);

    private final int value;

    private Signal(int v) { value = v; }

    public int getValue() { return value; }
  }

  private static void initialize() throws IOException {
    if (!NativeCodeLoader.isNativeCodeLoaded()) {
      throw new IOException("Native code not loaded!");
    }
  }

  public static int signal(int pid, Signal sig) throws IOException {
    initialize();
    int ret = kill(pid, sig.getValue());
    return ret;
  }

  public static int killProcessGroup(int pgid) throws IOException {
    initialize();
    int ret = signal(-pgid, Signal.SIGKILL);
    if (ret == 0) {
      LOG.info("Killing process group " + pgid + " returned 0");
    } else {
      LOG.warn("Killing process group " + pgid + " returned " + ret);
    }
    return ret;
  }

  private native static int kill(int pid, int sig);
}

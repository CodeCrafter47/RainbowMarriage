package org.projectrainbow.marriage;

import PluginReference.MC_Server;
import PluginReference.PluginBase;

public class MarriagePlugin extends PluginBase {

    @Override
    public void onStartup(MC_Server server) {
        MarryManager.LoadMarriageData();
        server.registerCommand(new CmdMarry());
        server.registerCommand(new CmdDivorce());
    }

    @Override
    public void onShutdown() {
        MarryManager.SaveMarriageData();
    }
}

/*
 * Copyright 2017 Benjamin Martin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.lapismc.lapisloginstrength;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import net.lapismc.lapislogin.api.events.ChangePasswordEvent;
import net.lapismc.lapislogin.api.events.LoginEvent;
import net.lapismc.lapislogin.api.events.RegisterEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class LapisLoginStrength extends JavaPlugin implements Listener {

    private Logger logger = getLogger();
    private Zxcvbn strengthChecker = new Zxcvbn();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
        logger.info(getDescription().getName() + " v." + getDescription().getVersion() + " has been enabled!");
    }

    @EventHandler
    public void onRegisterEvent(RegisterEvent e) {
        String password = e.getPassword();
        Strength s = strengthChecker.measure(password);
        if (s.getScore() < getConfig().getInt("MinimumScore")) {
            e.setCancelled(true, "your password is too weak " + s.getFeedback().getWarning());
            logger.info(e.getPlayer().getName() + " was not allowed to use " + e.getPassword() + " to register because " + s.getFeedback().getWarning());
        }
    }

    @EventHandler
    public void onLoginEvent(LoginEvent e) {
        if (!getConfig().getBoolean("LoginDeregister")) {
            return;
        }
        String password = e.getPassword();
        if (!e.getLoginPlayer().checkPassword(password)) {
            return;
        }
        Strength s = strengthChecker.measure(password);
        if (s.getScore() < getConfig().getInt("MinimumScore")) {
            e.setCancelled(true, "your password is too weak " + s.getFeedback().getWarning());
            e.getLoginPlayer().deregisterPlayer();
            e.getPlayer().sendMessage("Because your password is too weak, you need to register with /register (password) (password)");
            logger.info(e.getPlayer().getName() + " was not allowed to use " + e.getPassword() + " to login because " + s.getFeedback().getWarning());
        }
    }

    @EventHandler
    public void onPasswordChange(ChangePasswordEvent e) {
        String password = e.getNewPassword();
        Strength s = strengthChecker.measure(password);
        if (s.getScore() < getConfig().getInt("MinimumScore")) {
            e.setCancelled(true, "your password is too weak " + s.getFeedback().getWarning());
            logger.info(e.getPlayer().getName() + " was not allowed to change their password to " + e.getNewPassword() + " because " + s.getFeedback().getWarning());
        }
    }

}

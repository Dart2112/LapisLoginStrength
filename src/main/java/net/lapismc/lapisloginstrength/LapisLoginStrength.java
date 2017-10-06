package net.lapismc.lapisloginstrength;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
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
        Bukkit.getPluginManager().registerEvents(this, this);
        logger.info(getDescription().getName() + " v." + getDescription().getVersion() + " has been enabled!");
    }

    @EventHandler
    public void onRegisterEvent(RegisterEvent e) {
        String password = e.getPassword();
        Strength s = strengthChecker.measure(password);
        if (s.getScore() < getConfig().getInt("MinimumScore")) {
            e.setCancelled(true, "password is too weak " + s.getFeedback().getWarning());
            logger.info(e.getPlayer().getName() + " was not allowed to use " + e.getPassword() + " to register " + s.getFeedback().getWarning());
        }
    }

    @EventHandler
    public void onLoginEvent(LoginEvent e) {
        String password = e.getPassword();
        Strength s = strengthChecker.measure(password);
        if (s.getScore() < getConfig().getInt("MinimumScore")) {
            e.setCancelled(true, "password is too weak " + s.getFeedback().getWarning());
            e.getLoginPlayer().deregisterPlayer();
            logger.info(e.getPlayer().getName() + " was not allowed to use " + e.getPassword() + " to login " + s.getFeedback().getWarning());
        }
    }

}

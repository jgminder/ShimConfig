package modifiers;

import org.apache.log4j.Logger;
import util.PropertyHandler;
import util.ShimValues;

import java.io.File;

/**
 * Created by Ihar_Chekan on 10/14/2016.
 */
public class ModifyPluginConfigProperties {

    final static Logger logger = Logger.getLogger(ModifyPluginConfigProperties.class);

    public void modifyPluginProperties() {

        // Determine shim folder
        File f = new File(ShimValues.getPathToShim());
        String shimFolder = f.getName();
        File hadoopConfigurationsFolder = new File(f.getParent());
        String pluginPropertiesFile = hadoopConfigurationsFolder.getParent() + File.separator + "plugin.properties";
        String configPropertiesFile = ShimValues.getPathToShim() + "config.properties";

        PropertyHandler.setProperty(pluginPropertiesFile, "active.hadoop.configuration", shimFolder);

        PropertyHandler.setProperty(configPropertiesFile, "pentaho.oozie.proxy.user", "devuser");

        if (ShimValues.isShimSecured()) {
            String devuserKerberosPrincipal = "devuser@PENTAHOQA.COM";
            String hiveKerberosPrincipal = "hive@PENTAHOQA.COM";

            if (ShimValues.getHadoopVendor().equalsIgnoreCase("hdp") && Integer.valueOf(ShimValues.getHadoopVendorVersion()) == 30) {
                devuserKerberosPrincipal = "devuser@PENTAHO.NET";
                hiveKerberosPrincipal = "hive@PENTAHO.NET";
            }

            //determine if shim is using impersonation and modify it accordingly
            if (PropertyHandler.getPropertyFromFile(configPropertiesFile,
                    "pentaho.authentication.default.mapping.impersonation.type") == null) {
                PropertyHandler.setProperty(configPropertiesFile, "authentication.superuser.provider", "kerberos");
                PropertyHandler.setProperty(configPropertiesFile, "authentication.kerberos.id", "kerberos");
                PropertyHandler.setProperty(configPropertiesFile, "authentication.kerberos.principal", devuserKerberosPrincipal);
                PropertyHandler.setProperty(configPropertiesFile, "authentication.kerberos.password", "password");
            } else {
                PropertyHandler.setProperty(configPropertiesFile,
                        "pentaho.authentication.default.kerberos.principal", devuserKerberosPrincipal);
                PropertyHandler.setProperty(configPropertiesFile,
                        "pentaho.authentication.default.kerberos.password", "password");
                PropertyHandler.setProperty(configPropertiesFile,
                        "pentaho.authentication.default.mapping.impersonation.type", "simple");
                PropertyHandler.setProperty(configPropertiesFile,
                        "pentaho.authentication.default.mapping.server.credentials.kerberos.principal", hiveKerberosPrincipal);
                PropertyHandler.setProperty(configPropertiesFile,
                        "pentaho.authentication.default.mapping.server.credentials.kerberos.password", "password");
            }
        } else {
            //determine if shim is using impersonation and modify it accordingly
            if (PropertyHandler.getPropertyFromFile(configPropertiesFile,
                    "pentaho.authentication.default.mapping.impersonation.type") == null) {
                PropertyHandler.setProperty(configPropertiesFile, "authentication.superuser.provider", "NO_AUTH");
            } else {
                PropertyHandler.setProperty(configPropertiesFile,
                        "pentaho.authentication.default.mapping.impersonation.type", "disabled");
                PropertyHandler.setProperty(configPropertiesFile,
                        "pentaho.authentication.default.kerberos.password", "");
            }
        }

        // modifying /opt/pentaho/mapreduce in plugin.properties file
        if (ShimValues.getDfsInstallDir() != null && !"".equals(ShimValues.getDfsInstallDir())) {
            PropertyHandler.setProperty(pluginPropertiesFile, "pmr.kettle.dfs.install.dir",
                    "/opt/pentaho/mapreduce_" + ShimValues.getDfsInstallDir());
        }
    }
}

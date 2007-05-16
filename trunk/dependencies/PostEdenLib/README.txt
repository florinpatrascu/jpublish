EdenLib is a collection of classes which I use throughout my open source projects.

Please send all questions to me@anthonyeden.com or join the EdenLib developers list by visiting the web site http://lists.sourceforge.net/lists/listinfo/edenlib-developer

Sincerely,
Anthony Eden


== POST EdenLib ==
Starting from July 2006
 - Streamlined the XML parsing, added new methods for loading Configurations and fixing the UNICODE bugs.

2007-05-16
We'll temporary use this library until we will be able to streamline the JPublish code to use only functions developed inside core JPublish. Therefore the dependencies:

com.anthonyeden.lib.config.Configuration;
com.anthonyeden.lib.config.ConfigurationException;
com.anthonyeden.lib.config.XMLConfiguration;
com.anthonyeden.lib.resource.AbstractResourceLoader;
com.anthonyeden.lib.resource.FileResourceLoader;
com.anthonyeden.lib.resource.ResourceException;
com.anthonyeden.lib.resource.ResourceRecipient;
com.anthonyeden.lib.util.ClassUtilities;
com.anthonyeden.lib.util.IOUtilities;
com.anthonyeden.lib.util.SQLUtilities;

will be refactored into core JPublish objects.

Please send all questions to florin.patrascu@gmail.com

Thank you,
-florin
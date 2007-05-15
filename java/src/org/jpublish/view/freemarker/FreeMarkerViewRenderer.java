/*
 * Copyright 2004-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.jpublish.view.freemarker;

import java.io.Reader;
import java.io.Writer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import freemarker.template.Template;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;

import org.jpublish.Page;
import org.jpublish.SiteContext;
import org.jpublish.JPublishContext;
import org.jpublish.util.CharacterEncodingMap;
import org.jpublish.view.ViewRenderer;
import org.jpublish.view.ViewRenderException;

/** ViewRenderer which uses the FreeMarker template engine.
    
    @author Anthony Eden
    @since 2.0
*/

public class FreeMarkerViewRenderer implements ViewRenderer{
    
    private static final Log log = LogFactory.getLog(
        FreeMarkerViewRenderer.class);
    
    protected SiteContext siteContext;
    protected JPublishTemplateLoader templateLoader;
    protected freemarker.template.Configuration fmConfig;
    
    /** Set the SiteContext.
    
        @param siteContext The SiteContext
    */
    
    public void setSiteContext(SiteContext siteContext){
        this.siteContext = siteContext;
    }
    
    /** Initialize the ViewRenderer.
    
        @throws Exception Any Exception
    */
    
    public void init() throws Exception{
        fmConfig = new freemarker.template.Configuration();
        
        templateLoader = new JPublishTemplateLoader();
        templateLoader.setSiteContext(siteContext);
        fmConfig.setTemplateLoader(templateLoader);
        fmConfig.setLocalizedLookup(false);
    }
    
    /** Render the view.  This method will reparse the template text each time
        it is called.
    
        @param context The JPublishContext
        @param path The path to the template
        @param in The Reader to read view template from
        @param out The Writer to write the rendered view
        @throws IOException 
        @throws ViewRenderException
    */
    
    public void render(JPublishContext context, String path, Reader in,
    Writer out) throws IOException, ViewRenderException{
        CharacterEncodingMap characterEncodingMap = 
            siteContext.getCharacterEncodingManager().getMap(path);
        String encoding = characterEncodingMap.getPageEncoding();
        
        if(log.isDebugEnabled()){
            log.debug("render(" + path + ")");
            log.debug("Character encoding: " + encoding);
        }
        
        try{
            Page page = context.getPage();
            
            if(log.isDebugEnabled())
                log.debug("Locale: " + page.getLocale());
            
            Object viewContext = createViewContext(context, path);
            Template template = fmConfig.getTemplate(path, page.getLocale(),
                encoding);
            template.process(viewContext, out);
        } catch(IOException e){
            throw e;
        } catch(Exception e){
            throw new ViewRenderException(e);
        }
    }

    /** Render the view.
    
        @param context The JPublishContext
        @param path The path to the template
        @param in The InputStream to read view template from
        @param out The OutputStream to write the rendered view
        @throws IOException 
        @throws ViewRenderException
    */
    
    public void render(JPublishContext context, String path, InputStream in, 
    OutputStream out) throws IOException, ViewRenderException{
        render(context, path, new InputStreamReader(in), 
            new OutputStreamWriter(out));
    }
    
    /** Load the configuration for the view.
    
        @param configuration The configuration object
    */
    
    public void loadConfiguration(Configuration configuration)
    throws ConfigurationException{
        
    }
    
    /** Create the 'root' context for the template engine.  This method can be
        overridden in subclasses in case the viewContext needs to be populated
        with additional values.  The default implementation wraps the existing
        JPublishContext in a class which is useable by FreeMarker.

        @param context The JPublishContext
        @param path The path to the template
        @return Object The 'root' template context
        @throws ViewRenderException
    */
    
    protected Object createViewContext(JPublishContext context, 
    String path) throws ViewRenderException{
        FreeMarkerViewContext viewContext =
            new FreeMarkerViewContext(context);
        return viewContext;
    }
    
}

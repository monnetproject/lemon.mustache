/**
 * *******************************************************************************
 * Copyright (c) 2011, Monnet Project All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of the Monnet Project nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE MONNET PROJECT BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *******************************************************************************
 */
package eu.monnetproject.lemon.mustachegf.web;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;
import eu.monnetproject.lemon.tache.JenaResolverFactory;
import eu.monnetproject.lemon.tache.LemonGFTache;
import eu.monnetproject.lemon.tache.ResolverFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author John McCrae
 */
public class MustacheGFApp extends Application {

    @Override
    public void init() {
        final Window window = new Window("Lemon-Mustache-GF Generator");
        final Label title = new Label("Lemon Mustache GF mapping component");
        title.addStyleName(Reindeer.LABEL_H1);
        window.addComponent(title);

        final Upload ontoUpload = new Upload("Upload ontology file", ontoReceiver);
        window.addComponent(ontoUpload);
        ontoUpload.addListener((Upload.FailedListener) ontoReceiver);
        ontoUpload.addListener((Upload.SucceededListener) ontoReceiver);

        final Upload lexUpload = new Upload("Upload lexicon file", lexReceiver);
        window.addComponent(lexUpload);
        lexUpload.addListener((Upload.FailedListener) lexReceiver);
        lexUpload.addListener((Upload.SucceededListener) lexReceiver);

        final TextArea ontoSparql = new TextArea("Ontology SPARQL patterns");
        window.addComponent(ontoSparql);
        ontoSparql.setValue(new Scanner(MustacheGFApp.class.getResourceAsStream("/onto.sparql")).useDelimiter("\\Z").next());
        ontoSparql.setColumns(80);
        ontoSparql.setRows(30);

        final TextArea lexSparql = new TextArea("Lexicon SPARQL patterns");
        window.addComponent(lexSparql);
        lexSparql.setValue(new Scanner(MustacheGFApp.class.getResourceAsStream("/lex.sparql")).useDelimiter("\\Z").next());
        lexSparql.setColumns(80);
        lexSparql.setRows(30);

        final TextArea absMustache = new TextArea("Abstract Grammar Mustache patterns");
        window.addComponent(absMustache);
        absMustache.setValue(new Scanner(MustacheGFApp.class.getResourceAsStream("/abs.mustache")).useDelimiter("\\Z").next());
        absMustache.setColumns(80);
        absMustache.setRows(30);

        final TextArea conMustache = new TextArea("Concrete Grammar Mustache patterns");
        window.addComponent(conMustache);
        conMustache.setValue(new Scanner(MustacheGFApp.class.getResourceAsStream("/con.mustache")).useDelimiter("\\Z").next());
        conMustache.setColumns(80);
        conMustache.setRows(30);

        final TextField grammarName = new TextField("Grammar Name", "Example");
        window.addComponent(grammarName);

        final TextField language = new TextField("Language", "Eng");
        window.addComponent(language);

        final Button generate = new Button("Generate");
        window.addComponent(generate);

        final TextArea absGF = new TextArea("Abstract GF Grammar");
        window.addComponent(absGF);
        absGF.setColumns(80);
        absGF.setRows(30);

        final TextArea conGF = new TextArea("Concrete GF Grammar");
        window.addComponent(conGF);
        conGF.setColumns(80);
        conGF.setRows(30);

        generate.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    final ResolverFactory queryResolver;
                    if (ontoReceiver.file != null) {
                        if (lexReceiver.file != null) {
                            queryResolver = new JenaResolverFactory(ontoReceiver.file, lexReceiver.file);
                        } else {
                            window.showNotification("No lexicon", Window.Notification.TYPE_WARNING_MESSAGE);
                            queryResolver = new JenaResolverFactory(ontoReceiver.file);
                        }
                    } else {
                        if (lexReceiver.file != null) {
                            window.showNotification("No ontology", Window.Notification.TYPE_WARNING_MESSAGE);
                            queryResolver = new JenaResolverFactory(lexReceiver.file);
                        } else {
                            window.showNotification("No ontology or lexicon", Window.Notification.TYPE_WARNING_MESSAGE);
                            queryResolver = new JenaResolverFactory();
                        }
                    }
                    final HashMap<String, String> keys = new HashMap<String,String>();
                    keys.put("grammar", grammarName.getValue().toString());
                    keys.put("lang", language.getValue().toString());
                    final String absGrammar = LemonGFTache.convert(queryResolver, ontoSparql.getValue().toString(), absMustache.getValue().toString(), keys);
                    absGF.setValue(absGrammar);
                    final String conGrammar = LemonGFTache.convert(queryResolver, lexSparql.getValue().toString(), conMustache.getValue().toString(), keys);
                    conGF.setValue(conGrammar);
                } catch (Exception x) {
                    x.printStackTrace();
                    window.showNotification("Something went wrong: " + x.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });

        setMainWindow(window);
    }
    private final OntoLexReceiver ontoReceiver = new OntoLexReceiver();
    private final OntoLexReceiver lexReceiver = new OntoLexReceiver();

    private class OntoLexReceiver implements Upload.Receiver, Upload.SucceededListener, Upload.FailedListener {

        public File file;
        private File tmpFile;

        @Override
        public OutputStream receiveUpload(String filename, String mimeType) {
            try {
                tmpFile = File.createTempFile(filename, "");
                tmpFile.deleteOnExit();
                return new FileOutputStream(tmpFile);
            } catch (IOException x) {
                throw new RuntimeException(x);
            }

        }

        @Override
        public void uploadFailed(FailedEvent event) {
            file = null;
        }

        @Override
        public void uploadSucceeded(SucceededEvent event) {
            file = tmpFile;
        }
    };
}

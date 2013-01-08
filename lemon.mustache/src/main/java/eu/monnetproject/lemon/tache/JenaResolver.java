package eu.monnetproject.lemon.tache;
/**********************************************************************************
 * Copyright (c) 2011, Monnet Project
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Monnet Project nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE MONNET PROJECT BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *********************************************************************************/
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author John McCrae
 */
public class JenaResolver implements Resolver {
    private final Model model;
    private final String query;

    public JenaResolver(Model model, String query) {
        this.model = model;
        this.query = query;
    }

    
    @Override
    public List<Map<String, String>> execute() {
        final Query q = QueryFactory.create(query);
        final QueryExecution qexec = QueryExecutionFactory.create(q, model);
        final List<Map<String,String>> results = new ArrayList<Map<String, String>>();
        try {
            final ResultSet rs = qexec.execSelect();
            while(rs.hasNext()) {
                final QuerySolution soln = rs.nextSolution();
                final HashMap<String, String> map = new HashMap<String, String>();
                final Iterator<String> iterator = soln.varNames();
                while(iterator.hasNext()) {
                    final String varName = iterator.next();
                    final RDFNode node = soln.get(varName);
                    if(node.isLiteral()) {
                        map.put(varName, node.asLiteral().getString());
                    } else {
                        map.put(varName, node.asResource().getURI());
                    }
                }
                results.add(map);
            }
        } finally {
            qexec.close();
        }
        return results;
    }
    
    
}

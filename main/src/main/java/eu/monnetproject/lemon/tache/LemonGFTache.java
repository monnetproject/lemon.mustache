package eu.monnetproject.lemon.tache;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LemonGFTache {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            printUsage();
        } else {
            final String mustacheFileName = args[0].endsWith(".mustache") ? args[0] : (args[0] + ".mustache");
            final Template mustache = Mustache.compiler().escapeHTML(false).compile(new FileReader(mustacheFileName));
            final String sparqlFileName = args[0].endsWith(".mustache") ? args[0].replaceAll("\\.mustache$", ".sparql") : (args[0] + ".sparql");
            final ResolverFactory resolverFactory = args[1].startsWith("http") ? new SPARQLHTTPResolver(new URL(args[1]))
                    : new JenaResolverFactory(new File(args[1]));
            final LemonQuery lemon = new LemonQuery(resolverFactory, new FileReader(sparqlFileName));
            final String gfFileName = args[0].endsWith(".mustache") ? args[0].replaceAll("\\.mustache$", ".gf") : (args[0] + ".gf");
            final PrintWriter out = new PrintWriter(gfFileName);
            mustache.execute(lemon, out);
            out.close();
        }
    }

    /**
     * Convert using mustache and a lemon query
     *
     * @param mustache The mustache pattern
     * @param query The lemon query object
     * @return The resulting grammar file
     */
    public static String convert(String mustache, LemonQuery query) {
        final Template tmpl = Mustache.compiler().escapeHTML(false).compile(mustache);
        return tmpl.execute(query).trim();
    }

    public static String convert(ResolverFactory resolverFactory, String sparqlQueries, String mustacheFile) throws IOException {
        final LemonQuery query = new LemonQuery(resolverFactory, new StringReader(sparqlQueries));
        final Template tmpl = Mustache.compiler().escapeHTML(false).compile(mustacheFile);
        return tmpl.execute(query).trim();
    }
    
    
    public static String convert(ResolverFactory resolverFactory, String sparqlQueries, String mustacheFile, Map<String,String> properties) throws IOException {
        final LemonQuery query = new LemonQuery(resolverFactory, new StringReader(sparqlQueries));
        final Template tmpl = Mustache.compiler().escapeHTML(false).compile(mustacheFile);
        final HashMap<String, Object> query2 = new HashMap<String,Object>();
        query2.putAll(query);
        query2.putAll(properties);
        return tmpl.execute(query2).trim();
    }

    private static void printUsage() {
        System.err.println("Usage:\n\tmvn exec:java -Dexec.args=\"template.mustache http://sparql.lemon.endpoint/\"\n\t"
                + "mvn exec:java -Dexec.args=\"template.mustache ontolex.rdf");
    }
}

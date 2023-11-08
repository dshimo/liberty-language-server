package io.openliberty.tools.langserver;

import org.eclipse.lemminx.services.extensions.IXMLExtension;
import org.eclipse.lemminx.services.extensions.XMLExtensionsRegistry;
import org.eclipse.lemminx.services.extensions.diagnostics.IDiagnosticsParticipant;
import org.eclipse.lsp4j.InitializeParams;

public class LibertyLemminxServer implements IXMLExtension {
    private IDiagnosticsParticipant diagnosticsParticipant;


    @Override
    public void start(InitializeParams initializeParams, XMLExtensionsRegistry xmlExtensionsRegistry) {
        diagnosticsParticipant = new LibertyDiagnosticParticipant();
        xmlExtensionsRegistry.registerDiagnosticsParticipant(diagnosticsParticipant);
    }

    @Override
    public void stop(XMLExtensionsRegistry registry) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'stop'");
    }
    
}

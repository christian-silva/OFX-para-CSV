/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dados_xml;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Christian
 */
public class LeitorXML extends DefaultHandler {

    //StringBuilder conteudo;
    private String texto;
    private Map<String, String> TAG_valorTAG;
//    ArrayList<Produto> produtos = new ArrayList<>();
//    Produto produto;

    /**
     * evento startDocument do SAX. Disparado antes do processamento da primeira
     * linha
     */
    @Override
    public void startDocument() {
        //System.out.println("Iniciando o Parsing...");
        TAG_valorTAG = new HashMap<>();
    }

    /**
     * evento endDocument do SAX. Disparado depois do processamento da última
     * linha
     */
    @Override
    public void endDocument() {
       // System.out.println("Fim do Parsing...");
//        Set<String> TAGS = TAG_valorTAG.keySet();
//        for (String TAG : TAGS) {
//            System.out.println("TAG: " + TAG + " Valor da TAG: " + TAG_valorTAG.get(TAG));
//            
//        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
     //   System.out.println("[TAG: " + qName +"]");
 //        if (qName.equals("produto")) {
//            produto = new Produto();
//        }
        //conteudo = new StringBuilder();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        
        texto = new String(ch, start, length);
        //System.out.println("[ ---- Valor: " + texto.trim().toLowerCase() +"]");
		// ------------------------------------------------------------
		// --- TRATAMENTO DAS INFORMAÇÕES DE ACORDO COM A TAG ATUAL ---
		// ------------------------------------------------------------
       // conteudo.append(new String(ch, start, length));
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
         TAG_valorTAG.put(qName.trim().toLowerCase(), texto.trim().toLowerCase());
        
    }

    public Map<String, String> getTAG_valorTAG() {
        return TAG_valorTAG;
    }
    
}

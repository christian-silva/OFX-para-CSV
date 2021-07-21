/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dados_xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Christian
 */
public class FuncaoConvertedora {

    public static ArrayList<String> converter(String arquivo) {

        ArrayList<String> linhasOFX = new ArrayList<>();
        BufferedReader br = null;
        PrintWriter writer = null;
        String destino = System.getProperty("java.io.tmpdir");
        Date agora = new Date();
        String arquivoNovo = "ofx_temporario_" + agora.getTime() + ".xml";
        /**
         * Geração do arquivo XML
         */
        try {
            br = new BufferedReader(new FileReader(arquivo));
            arquivoNovo = destino + "\\" + arquivoNovo;
            writer = new PrintWriter(arquivoNovo, "UTF-8");
            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            String str;
            while ((str = br.readLine()) != null) {
                if ((str.trim().length() > 0) && (str.trim().startsWith("<"))) {
                    if (!str.trim().endsWith(">")) {
                        int inicio = str.trim().lastIndexOf("<") + 1;
                        int fim = str.trim().lastIndexOf(">") + 1;
                        String finaltag = "</" + str.trim().substring(inicio, fim);
                        str = str + finaltag;
                    }
                    writer.println(str);
                }
            }
            writer.close();
            br.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FuncaoConvertedora.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(FuncaoConvertedora.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FuncaoConvertedora.class.getName()).log(Level.SEVERE, null, ex);
        }

        File XML = new File(arquivoNovo);
        String nomeBanco = "";
        String agencia = "";
        String conta = "";
        String saldoFinal = "";

        try {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document documento = builder.parse(XML);
            documento.getDocumentElement().normalize();

            NodeList listaDeAtributos = documento.getElementsByTagName("FI");
            int tamanhoLista = listaDeAtributos.getLength();

            /**
             * Obtem o nome do banco
             */
            for (int i = 0; i < tamanhoLista; i++) {
                Node infProt = listaDeAtributos.item(i);
                if (infProt.getNodeType() == Node.ELEMENT_NODE) {
                    try {
                        Element elementoXML = (Element) infProt;
                        NodeList interno = elementoXML.getElementsByTagName("ORG");
                        String dataTemp = interno.item(0).getTextContent();
                        nomeBanco = dataTemp;
                    } catch (DOMException e) {
                        System.err.println("ORG - " + e.getMessage());
                    }
                }
            }

            /**
             * Obtem a agencia e conta do banco
             */
            listaDeAtributos = documento.getElementsByTagName("BANKACCTFROM");
            tamanhoLista = listaDeAtributos.getLength();

            for (int i = 0; i < tamanhoLista; i++) {
                Node infProt = listaDeAtributos.item(i);
                if (infProt.getNodeType() == Node.ELEMENT_NODE) {
                    try {
                        Element elementoXML = (Element) infProt;
                        NodeList interno = elementoXML.getElementsByTagName("BRANCHID");
                        String dataTemp = interno.item(0).getTextContent();
                        agencia = dataTemp;
                    } catch (Exception e) {
                        System.err.println("BRANCHID - " + e);
                    }
                }
            }
            for (int i = 0; i < tamanhoLista; i++) {
                Node infProt = listaDeAtributos.item(i);
                if (infProt.getNodeType() == Node.ELEMENT_NODE) {
                    try {
                        Element elementoXML = (Element) infProt;
                        NodeList interno = elementoXML.getElementsByTagName("ACCTID");
                        String dataTemp = interno.item(0).getTextContent();
                        conta = dataTemp;
                    } catch (Exception e) {
                        System.err.println("ACCTID - " + e.getMessage());
                    }
                }
            }

            /**
             * Obtem o saldo final da conta
             */
            listaDeAtributos = documento.getElementsByTagName("LEDGERBAL");
            tamanhoLista = listaDeAtributos.getLength();

            for (int i = 0; i < tamanhoLista; i++) {
                Node infProt = listaDeAtributos.item(i);
                if (infProt.getNodeType() == Node.ELEMENT_NODE) {
                    try {
                        Element elementoXML = (Element) infProt;
                        NodeList interno = elementoXML.getElementsByTagName("BALAMT");
                        String dataTemp = interno.item(0).getTextContent();
                        saldoFinal = dataTemp.replace(".", ",");;
                    } catch (Exception e) {
                        System.err.println("BALAMT - " + e.getMessage());
                    }
                }
            }
            /**
             * Insere os primeiros dados da conta e cabeçalho
             */

            //String inserir = "Nome do Banco;Agencia;Conta;Saldo Final";
            //linhasOFX.add(inserir);
            //inserir = nomeBanco + ";" + agencia + ";" + conta + ";" + saldoFinal;
            //System.out.println("inserir banco: " + inserir);
            //linhasOFX.add(inserir);
            //inserir = "";
            //linhasOFX.add(inserir);
            linhasOFX.add(getCabecalhoOFX());

            /**
             * Obtem os dados das transações do arquivo
             */
            listaDeAtributos = documento.getElementsByTagName("STMTTRN");
            tamanhoLista = listaDeAtributos.getLength();
            System.out.println("-----------------------------------------------------------------");
            for (int i = 0; i < tamanhoLista; i++) {
                Node node = listaDeAtributos.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;
                    String TRNTYPE = null;
                    String DTPOSTED = null;
                    String TRNAMT = null;
                    String IDENT = null;
                    String REFNUM = null;
                    String MEMO = null;

                    //tipo
                    try {
                        TRNTYPE = element.getElementsByTagName("TRNTYPE").item(0).getTextContent();
                    } catch (Exception e) {
                        System.err.println("TRNTYPE - " + e.getMessage());
                        TRNTYPE = "";
                    }
                    try {
                        //data
                        DTPOSTED = element.getElementsByTagName("DTPOSTED").item(0).getTextContent().substring(0, 8);
                        DTPOSTED = DTPOSTED.substring(6, 8) + "/" + DTPOSTED.substring(4, 6) + "/" + DTPOSTED.substring(0, 4);
                    } catch (DOMException e) {
                        System.err.println("DTPOSTED - " + e.getMessage());
                        DTPOSTED = "";
                    }
                    try {
                        //valor
                        TRNAMT = element.getElementsByTagName("TRNAMT").item(0).getTextContent().replace(".", ",");
                    } catch (Exception e) {
                        System.err.println("TRNAMT - " + e.getMessage());
                        TRNAMT = "";
                    }
                    
                    try {
                        //Ident
                        IDENT = element.getElementsByTagName("FITID").item(0).getTextContent();
                    } catch (Exception e) {
                        System.err.println("FITID - " + e.getMessage());
                        IDENT = "";
                    }
                    try {
                        //Referencia
                        REFNUM = element.getElementsByTagName("REFNUM").item(0).getTextContent().replace(".", "");
                    } catch (Exception e) {
                        System.err.println("REFNUM - " + e.getMessage());
                        REFNUM = "";
                    }
                    try {
                        //Descrição
                        MEMO = element.getElementsByTagName("MEMO").item(0).getTextContent();
                    } catch (Exception e) {
                        System.err.println("MEMO - " + e.getMessage());
                        MEMO = "";
                    }

                    String linha = DTPOSTED + ";" + TRNTYPE + ";" + IDENT + ";" + REFNUM + ";" + MEMO + ";" + TRNAMT + ";" 
                            +nomeBanco+ ";" +agencia+ ";" +conta+ ";" + saldoFinal;
                    //System.out.println("linha: " + linha);
                    linhasOFX.add(linha);

                }
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(FuncaoConvertedora.class.getName()).log(Level.SEVERE, null, ex);
        }
        return linhasOFX;
    }

    private static String getCabecalhoOFX() {
        String cabecalho = "DATA;TIPO;IDENT;REFERENCIA;DESCRICAO;VALOR;BANCO;AGENCIA;CONTA;SALDO_FINAL";
        return cabecalho;
    }
    
}

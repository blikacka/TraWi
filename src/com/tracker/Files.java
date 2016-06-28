package com.tracker;

import android.content.Context;
import android.util.Xml;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

/**
 *
 * @author Notasek
 */
public class Files {

    Context context;
    String filename = "traviconfig.xml";
    String filenameCoords = "traviCoordsConfig.xml";
    String filenameSms = "traviSMSConfig.xml";
    String path = "/sdcard/TraWi/";

    public Files(Context context) {
        this.context = context;
    }

    public void saveToXMLNumbers(ArrayList<FilesSetter> filesSetter) {

        try {
            File fileDirectory = new File(path);
            fileDirectory.mkdirs();
            File outputFile = new File(fileDirectory, filename);

            FileOutputStream fos = new FileOutputStream(outputFile);

//            fos = context.openFileOutput(filename, context.MODE_WORLD_READABLE);
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "UTF-8");
            serializer.startDocument(null, true);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

            serializer.startTag(null, "numbers");

            for (int i = 0; i < filesSetter.size(); i++) {
                serializer.startTag(null, "phone");

                serializer.startTag(null, "color").text(String.valueOf(filesSetter.get(i).getColor())).endTag(null, "color");
                serializer.startTag(null, "alias").text(filesSetter.get(i).getAlias()).endTag(null, "alias");
                serializer.startTag(null, "number").text(filesSetter.get(i).getNumber()).endTag(null, "number");

                serializer.endTag(null, "phone");
            }

            serializer.endDocument();
            serializer.flush();
            fos.close();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } catch (IllegalArgumentException ex) {
        } catch (IllegalStateException ex) {
        }
    }

    public ArrayList<FilesSetter> readFromXMLNumbers() {
        try {
            File file = new File(path + filename);
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = null;

            String data;

//            fis = context.openFileInput(path + filename);
            isr = new InputStreamReader(fis);
            char[] inputBuffer = new char[fis.available()];
            isr.read(inputBuffer);
            data = new String(inputBuffer);
            isr.close();
            fis.close();

            InputStream is = new ByteArrayInputStream(data.getBytes("UTF-8"));

            DocumentBuilderFactory dbf;
            DocumentBuilder db;
            NodeList items = null;
            Document dom;

            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            dom = db.parse(is);
            // normalize the document
            dom.getDocumentElement().normalize();

            items = dom.getElementsByTagName("phone");

            ArrayList<FilesSetter> filesSetter = new ArrayList<FilesSetter>();

            for (int i = 0; i < items.getLength(); i++) {
                FilesSetter fs = new FilesSetter();

                Node firstPhoneNode = items.item(i);
                if (firstPhoneNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element firstPhoneElement = (Element) firstPhoneNode;

                    NodeList firstColorList = firstPhoneElement.getElementsByTagName("color");
                    Element firstColorElement = (Element) firstColorList.item(0);
                    NodeList color = firstColorElement.getChildNodes();
                    fs.setColor(Integer.parseInt(((Node) color.item(0)).getNodeValue().trim()));

                    NodeList firstAliasList = firstPhoneElement.getElementsByTagName("alias");
                    Element firstAliasElement = (Element) firstAliasList.item(0);
                    NodeList alias = firstAliasElement.getChildNodes();
                    fs.setAlias(((Node) alias.item(0)).getNodeValue().trim());

                    NodeList firstNumberList = firstPhoneElement.getElementsByTagName("number");
                    Element firstNumberElement = (Element) firstNumberList.item(0);
                    NodeList number = firstNumberElement.getChildNodes();
                    fs.setNumber(((Node) number.item(0)).getNodeValue().trim());

                    filesSetter.add(fs);

                }
            }
            return filesSetter;
        } catch (FileNotFoundException ex) {
//            ArrayList<FilesSetter> filesSetter = new ArrayList<FilesSetter>();
//            FilesSetter fs = new FilesSetter();
//            fs.setAlias(" ");
//            fs.setColor(0);
//            fs.setNumber(" ");
//            filesSetter.add(fs);
//            saveToXMLNumbers(filesSetter);
        } catch (IOException ex) {
        } catch (ParserConfigurationException ex) {
        } catch (SAXException ex) {
        }
        return null;
    }

    public void saveToXMLDefaultCoords(FilesDefaultCoordsSetter filesSetter) {

        try {
            File fileDirectory = new File(path);
            fileDirectory.mkdirs();
            File outputFile = new File(fileDirectory, filenameCoords);

            FileOutputStream fos = new FileOutputStream(outputFile);

//            fos = context.openFileOutput(filename, context.MODE_WORLD_READABLE);
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "UTF-8");
            serializer.startDocument(null, true);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

            serializer.startTag(null, "coords");

            serializer.startTag(null, "defaultLat").text(String.valueOf(filesSetter.getDefaultLat())).endTag(null, "defaultLat");
            serializer.startTag(null, "defaultLng").text(String.valueOf(filesSetter.getDefaultLng())).endTag(null, "defaultLng");

            serializer.endDocument();
            serializer.flush();
            fos.close();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } catch (IllegalArgumentException ex) {
        } catch (IllegalStateException ex) {
        }
    }

    public FilesDefaultCoordsSetter readFromXMLDefaultCoords() {
        try {
            File file = new File(path + filenameCoords);
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = null;

            String data;

            isr = new InputStreamReader(fis);
            char[] inputBuffer = new char[fis.available()];
            isr.read(inputBuffer);
            data = new String(inputBuffer);
            isr.close();
            fis.close();

            InputStream is = new ByteArrayInputStream(data.getBytes("UTF-8"));

            DocumentBuilderFactory dbf;
            DocumentBuilder db;
            NodeList items = null;
            Document dom;

            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            dom = db.parse(is);
            // normalize the document
            dom.getDocumentElement().normalize();

            items = dom.getElementsByTagName("coords");
            FilesDefaultCoordsSetter fs = new FilesDefaultCoordsSetter();

            for (int i = 0; i < items.getLength(); i++) {

                Node firstPhoneNode = items.item(i);
                if (firstPhoneNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element firstPhoneElement = (Element) firstPhoneNode;

                    NodeList firstColorList = firstPhoneElement.getElementsByTagName("defaultLat");
                    Element firstColorElement = (Element) firstColorList.item(0);
                    NodeList color = firstColorElement.getChildNodes();
                    fs.setDefaultLat(Double.parseDouble(((Node) color.item(0)).getNodeValue().trim()));

                    NodeList firstAliasList = firstPhoneElement.getElementsByTagName("defaultLng");
                    Element firstAliasElement = (Element) firstAliasList.item(0);
                    NodeList alias = firstAliasElement.getChildNodes();
                    fs.setDefaultLng(Double.parseDouble(((Node) alias.item(0)).getNodeValue().trim()));

                }
            }
            return fs;
        } catch (FileNotFoundException ex) {
//            FilesDefaultCoordsSetter filesSetter = new FilesDefaultCoordsSetter();
//            filesSetter.setDefaultLat(0);
//            filesSetter.setDefaultLng(0);
//            saveToXMLDefaultCoords(filesSetter);
        } catch (IOException ex) {
        } catch (ParserConfigurationException ex) {
        } catch (SAXException ex) {
        }
        return null;
    }

    public void saveToXMLSms(ArrayList<FilesSmsSetter> filesSetter) {

        try {
            File fileDirectory = new File(path);
            fileDirectory.mkdirs();
            File outputFile = new File(fileDirectory, filenameSms);

            FileOutputStream fos = new FileOutputStream(outputFile);

//            fos = context.openFileOutput(filename, context.MODE_WORLD_READABLE);
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "UTF-8");
            serializer.startDocument(null, true);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

            serializer.startTag(null, "smsset");

            for (int i = 0; i < filesSetter.size(); i++) {
                serializer.startTag(null, "sms");

                serializer.startTag(null, "smsFormat").text(filesSetter.get(i).getSmsFormat()).endTag(null, "smsFormat");
                serializer.startTag(null, "smsAlias").text(filesSetter.get(i).getSmsAlias()).endTag(null, "smsAlias");
                serializer.startTag(null, "smsNumber").text(String.valueOf(filesSetter.get(i).getSmsNumber())).endTag(null, "smsNumber");

                serializer.endTag(null, "sms");
            }

            serializer.endDocument();
            serializer.flush();
            fos.close();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } catch (IllegalArgumentException ex) {
        } catch (IllegalStateException ex) {
        }
    }

    public ArrayList<FilesSmsSetter> readFromXMLSms() {
        try {
            File file = new File(path + filenameSms);
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = null;

            String data;

//            fis = context.openFileInput(path + filename);
            isr = new InputStreamReader(fis);
            char[] inputBuffer = new char[fis.available()];
            isr.read(inputBuffer);
            data = new String(inputBuffer);
            isr.close();
            fis.close();

            InputStream is = new ByteArrayInputStream(data.getBytes("UTF-8"));

            DocumentBuilderFactory dbf;
            DocumentBuilder db;
            NodeList items = null;
            Document dom;

            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            dom = db.parse(is);
            // normalize the document
            dom.getDocumentElement().normalize();

            items = dom.getElementsByTagName("sms");

            ArrayList<FilesSmsSetter> filesSetter = new ArrayList<FilesSmsSetter>();

            for (int i = 0; i < items.getLength(); i++) {
                FilesSmsSetter fs = new FilesSmsSetter();

                Node firstPhoneNode = items.item(i);
                if (firstPhoneNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element firstPhoneElement = (Element) firstPhoneNode;

                    NodeList firstFirstList = firstPhoneElement.getElementsByTagName("smsFormat");
                    Element firstFirstElement = (Element) firstFirstList.item(0);
                    NodeList first = firstFirstElement.getChildNodes();
                    fs.setSmsFormat(((Node) first.item(0)).getNodeValue().trim());

                    NodeList firstSecondList = firstPhoneElement.getElementsByTagName("smsAlias");
                    Element firstSecondElement = (Element) firstSecondList.item(0);
                    NodeList second = firstSecondElement.getChildNodes();
                    fs.setSmsAlias(((Node) second.item(0)).getNodeValue().trim());

                    NodeList firstThirdList = firstPhoneElement.getElementsByTagName("smsNumber");
                    Element firstThirdElement = (Element) firstThirdList.item(0);
                    NodeList third = firstThirdElement.getChildNodes();
                    fs.setSmsNumber(Integer.parseInt(((Node) third.item(0)).getNodeValue().trim()));

                    filesSetter.add(fs);

                }
            }
            return filesSetter;
        } catch (FileNotFoundException ex) {
//            ArrayList<FilesSmsSetter> filesSetter = new ArrayList<FilesSmsSetter>();
//            FilesSmsSetter fsf = new FilesSmsSetter();
//            fsf.setSmsAlias(" ");
//            fsf.setSmsFormat(" ");
//            fsf.setSmsNumber(0);
//            filesSetter.add(fsf);
//            saveToXMLSms(filesSetter);
        } catch (IOException ex) {
        } catch (ParserConfigurationException ex) {
        } catch (SAXException ex) {
        }
        return null;
    }

    public boolean isFileExist() {
        File file = new File(path + filename);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }
}

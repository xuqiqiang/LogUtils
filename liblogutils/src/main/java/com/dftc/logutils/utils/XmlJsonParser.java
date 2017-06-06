package com.dftc.logutils.utils;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Created by xuqiqiang on 2017/6/2.
 */
public class XmlJsonParser {

    public static String xml(String xml) {
        if (TextUtils.isEmpty(xml)) {
            return "Empty/Null xml content.(This msg from LogUtils)";
        }
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString(ObjParser.INDENT));
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString().replaceFirst(">", ">\n");
        } catch (TransformerException e) {
            return e.getCause().getMessage() + "\n" + xml;
        }
    }

    public static String json(String json) {
        if (TextUtils.isEmpty(json)) {
            return "Empty/Null json content.(This msg from LogUtils)";
        }
        try {
            if (json.startsWith("{")) {
                return new JSONObject(json).toString(ObjParser.INDENT);
            } else if (json.startsWith("[")) {
                return new JSONArray(json).toString(ObjParser.INDENT);
            }
        } catch (JSONException e) {
            return e.getCause().getMessage() + "\n" + json;
        }
        return "Log error!(This msg from LogUtils)";
    }
}

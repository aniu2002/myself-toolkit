package com.sparrow.collect.document;

import java.util.Arrays;
import java.util.List;


/**
 */
public class IdxField {
    
    private String field;
    
    private String[] dataKeys;
    
    private List<String> textProcessor;
    
    private String[] strategies;
    
    public IdxField(String field) {
        this.field = field;
    }

    public IdxField(String field, List<String> textProcessor, String[] strategies) {
        this.field = field;
        this.textProcessor = textProcessor;
        this.strategies = strategies;
    }

    public IdxField(String field, String[] strategies, List<String> textProcessor, String[] dataKeys) {
        super();
        this.field = field;
        this.strategies = strategies;
        if (textProcessor !=null) {
            this.textProcessor = textProcessor;
        }
        this.dataKeys = dataKeys;
    }
    
    public String[] getStrategies() {
        return strategies;
    }

    
    public void setStrategies(String[] strategies) {
        this.strategies = strategies;
    }

    public List<String> getTextProcessor() {
        return textProcessor;
    }
    
    public void setTextProcessor(List<String> textProcessor) {
        this.textProcessor = textProcessor;
    }
    
    public void setTextProcessor(String strPro) {
        this.textProcessor.add(strPro);
    }

    public String getField() {
        return field;
    }
    
    public void setField(String field) {
        this.field = field;
    }
    
    public String[] getDataKeys() {
        return dataKeys;
    }
    
    public void setDataKeys(String[] dataKeys) {
        this.dataKeys = dataKeys;
    }

    @Override
    public String toString() {
        return "IdxField [field=" + field + ", textProcessor=" + textProcessor
                + ", strategy=" + Arrays.asList(strategies) + ", dataKeys=" + Arrays.asList(dataKeys)+ "]";
    }
    
}

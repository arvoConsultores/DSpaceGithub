package es.arvo.orcid;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import org.apache.commons.lang.builder.ToStringBuilder;

public class ErrorDescVO {
   
    @Expose
    private String content;

    /**
    *
    * @return
    * The content
    */
    public String getContent() {
    return content;
    }

    /**
    *
    * @param content
    * The content
    */
    public void setContent(String content) {
    this.content = content;
    }

    @Override
    public String toString() {
    return ToStringBuilder.reflectionToString(this);
    }

    }


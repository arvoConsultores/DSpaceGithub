package org.dspace.app.xmlui.aspect.eperson;

import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Body;
import org.dspace.app.xmlui.wing.element.Division;
import org.dspace.app.xmlui.wing.element.PageMeta;

public class OrcidAccessReceived extends AbstractDSpaceTransformer{
    
    /** Language string */
    private static final Message T_title =
        message("xmlui.OrcidAccessReceived.title");
    
    private static final Message T_dspace_home =
        message("xmlui.general.dspace_home");
    
    private static final Message T_trail =
        message("xmlui.OrcidAccessReceived.trail");
    
    private static final Message T_head =
        message("xmlui.OrcidAccessReceived.head");
    
    private static final Message T_para1 =
        message("xmlui.OrcidAccessReceived.para1");
    
    private static final Message T_go_home =
        message("xmlui.general.go_home");
    
    public void addPageMeta(PageMeta pageMeta) throws WingException
    {
      // Set the page title
      pageMeta.addMetadata("title").addContent(T_title);

      pageMeta.addTrailLink(contextPath + "/", T_dspace_home);
      pageMeta.addTrail().addContent(T_trail);
    }

    public void addBody(Body body) throws WingException
    {
      Division token = body.addDivision("orcid-access-token", "primary");

      token.setHead(T_head);

      token.addPara(T_para1);

      token.addPara().addXref(contextPath + "/", T_go_home);
    }
}

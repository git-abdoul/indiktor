package com.icesoft.faces.component.outputchart;

import java.beans.Beans;
import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.w3c.dom.Element;

import com.icesoft.faces.component.ExtendedAttributeConstants;
import com.icesoft.faces.component.ext.renderkit.FormRenderer;
import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.renderkit.dom_html_basic.DomBasicRenderer;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import com.icesoft.faces.renderkit.dom_html_basic.PassThruAttributeRenderer;

public class OutputChartRenderer extends DomBasicRenderer {
	private static final String[] passThruAttributes = ExtendedAttributeConstants.getAttributes(ExtendedAttributeConstants.ICE_OUTPUTCHART);

	public void encodeBegin(FacesContext facesContext, UIComponent uiComponent) throws IOException {
		OutputChart outputChart = (OutputChart) uiComponent;
		if (!Beans.isDesignTime()) {
			try {
				if (outputChart.getAbstractChart() == null) {
					outputChart.createAbstractChart();
					if (outputChart.getType().equalsIgnoreCase(OutputChart.CUSTOM_CHART_TYPE)) {
						outputChart.evaluateRenderOnSubmit(facesContext);
					}
					outputChart.getAbstractChart().encode(facesContext,outputChart);
				} else if (outputChart.evaluateRenderOnSubmit(facesContext).booleanValue()) {
					outputChart.getAbstractChart().encode(facesContext,	outputChart);
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		String clientId = outputChart.getClientId(facesContext);
		DOMContext domContext = DOMContext.attachDOMContext(facesContext, uiComponent);
		if (!domContext.isInitialized()) {
			Element table = domContext.createElement(HTML.TABLE_ELEM);
			domContext.setRootNode(table);
			setRootElementId(facesContext, table, uiComponent);
			Element tbody = (Element) domContext.createElement(HTML.TBODY_ELEM);
			Element tr = (Element) domContext.createElement(HTML.TR_ELEM);
			Element td = (Element) domContext.createElement(HTML.TD_ELEM);
			table.setAttribute(HTML.CLASS_ATTR, outputChart.getStyleClass());
			String style = outputChart.getStyle();
			if (style != null && style.length() > 0)
				table.setAttribute(HTML.STYLE_ATTR, style);
			else
				table.removeAttribute(HTML.STYLE_ATTR);
			table.appendChild(tbody);
			tbody.appendChild(tr);
			tr.appendChild(td);
		}
		Element table = (Element) domContext.getRootNode();
		FormRenderer.addHiddenField(facesContext, OutputChart.ICE_CHART_COMPONENT);

		Element td = (Element) domContext.getRootNode(). // table
				getFirstChild().// tbody Art:
				getFirstChild().// tr
				getFirstChild();// td
		DOMContext.removeChildren(td);
		PassThruAttributeRenderer.renderHtmlAttributes(facesContext, uiComponent, td, table, passThruAttributes);
		Element image = (Element) domContext.createElement(HTML.IMG_ELEM);
		
		if (outputChart.getChartURI() != null)
			image.setAttribute(HTML.SRC_ATTR, outputChart.getChartURI().getPath());

		td.appendChild(image);
		if (outputChart.isClientSideImageMap()) {
			Element map = (Element) domContext.createElement(HTML.MAP_ELEM);
			map.setAttribute(HTML.NAME_ATTR, "map" + clientId);
			image.setAttribute(HTML.USEMAP_ATTR, "#map" + clientId);
			image.setAttribute(HTML.BORDER_ATTR, "0");
			// render the clientSideImageMap if the component has an
			// actionListener registered
			outputChart.generateClientSideImageMap(domContext, map);
			td.appendChild(map);
		}
		domContext.stepOver();
	}
}

/**
 * 
 */
package com.tenline.pinecone.platform.web.store.client.widgets;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.RootPanel;
import com.tenline.pinecone.platform.web.store.client.Images;
import com.tenline.pinecone.platform.web.store.client.Messages;

/**
 * @author Bill
 *
 */
public abstract class AbstractViewport extends Viewport {
	
	protected Header header = new Header();
	protected Body body = new Body();
	protected Footer footer = new Footer();

	/**
	 * 
	 */
	public AbstractViewport() {
		// TODO Auto-generated constructor stub
		setLayout(new BorderLayout());
		add(header, new BorderLayoutData(LayoutRegion.NORTH, 60));
		add(body, new BorderLayoutData(LayoutRegion.CENTER));
		add(footer, new BorderLayoutData(LayoutRegion.SOUTH, 50));
	}
	
	/**
	 * 
	 */
	public void updateToRootPanel() {
		RootPanel.get().clear();
		RootPanel.get().add(this);
	}
	
	/**
	 * 
	 * @param event
	 */
	public abstract void handleViewCallback(AppEvent event); 
	
	/**
	 * 
	 * @author Bill
	 *
	 */
	protected class Header extends ToolBar {
		
		protected Header() {
			Button logo = new Button();
			logo.setIcon(((Images) Registry.get(Images.class.getName())).logo());
			logo.setWidth(260);
			logo.setHeight(55);
			add(logo);
			add(new FillToolItem());
		}
		
	}
	
	/**
	 * 
	 * @author Bill
	 *
	 */
	protected class Body extends ContentPanel {
		
		protected Body() {
			setHeaderVisible(false);
			setLayout(new BorderLayout());
		}
		
	}
	
	/**
	 * 
	 * @author Bill
	 *
	 */
	protected class Footer extends ToolBar {
		
		protected Footer() {
			setLayout(new CenterLayout());
			add(new LabelField(((Messages) Registry.get(Messages.class.getName())).copyright()));
		}
		
	}

}
package com.tenline.pinecone.fishshow.application.client;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class CameraPortlet extends Portlet {
	public CameraPortlet() {
		setSize("450", "320");
		setCollapsible(true);
		setLayout(new FitLayout());
		
		CameraPanel cameraPanel = new CameraPanel();
		add(cameraPanel);
		
		ToolButton tBtn = new ToolButton("x-tool-save");
		tBtn.addListener(Events.Select, new Listener<ButtonEvent>() {
			public void handleEvent(ButtonEvent e) {
				
			}
		});
		this.getHeader().addTool(tBtn);
		
	}

	
}
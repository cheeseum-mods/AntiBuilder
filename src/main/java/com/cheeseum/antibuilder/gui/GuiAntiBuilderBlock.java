package com.cheeseum.antibuilder.gui;

import com.cheeseum.antibuilder.AntiBuilder;
import com.cheeseum.antibuilder.TileEntityAntiBuilder;
import com.cheeseum.antibuilder.network.AntiBuilderMsgUpdate;

import cpw.mods.fml.client.config.GuiSlider;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;

public class GuiAntiBuilderBlock extends GuiScreen {
	private TileEntityAntiBuilder teAntiBuilder;
	private TileEntityAntiBuilder.OffsetData offsetData;
	
	// padding (or margin w/e) and internal placement offsets
	public int paddingX = 0;
	public int paddingY = 0;
	private int offsetXcounter = 0;
	private int offsetYcounter = 0;
	
	// gui elements
	private enum ELEMENTS {
		XNEGOFFSET, XPOSOFFSET,
		YNEGOFFSET, YPOSOFFSET,
		ZNEGOFFSET, ZPOSOFFSET,
		DONEBTN, CANCELBTN
	}
	
	public GuiAntiBuilderBlock(TileEntityAntiBuilder te) {
		this.teAntiBuilder = te;
		this.offsetData = new TileEntityAntiBuilder.OffsetData(te.offsets);
	}
	
	public void initGui() {
		int button_width = 300;
		this.paddingX = 1;
		this.paddingY = 5;
	
		// need this to calculate center
		ScaledResolution sres = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
		
		// initial padding
		this.offsetXcounter = (sres.getScaledWidth() - button_width) / 2;
		this.offsetYcounter = 20;
	
		// add all the buttons
		this.addButton(new GuiSlider(ELEMENTS.XNEGOFFSET.ordinal(), 0, 0, button_width, 20, "X- Offset: ", "", 0.0, 128.0, offsetData.xNeg, false, true));
		this.addButton(new GuiSlider(ELEMENTS.XPOSOFFSET.ordinal(), 0, 0, button_width, 20, "X+ Offset: ", "", 0.0, 128.0, offsetData.xPos, false, true));
		this.addButton(new GuiSlider(ELEMENTS.YNEGOFFSET.ordinal(), 0, 0, button_width, 20, "Y- Offset: ", "", 0.0, 128.0, offsetData.yNeg, false, true));
		this.addButton(new GuiSlider(ELEMENTS.YPOSOFFSET.ordinal(), 0, 0, button_width, 20, "Y+ Offset: ", "", 0.0, 128.0, offsetData.yPos, false, true));
		this.addButton(new GuiSlider(ELEMENTS.ZNEGOFFSET.ordinal(), 0, 0, button_width, 20, "Z- Offset: ", "", 0.0, 128.0, offsetData.zNeg, false, true));
		this.addButton(new GuiSlider(ELEMENTS.ZPOSOFFSET.ordinal(), 0, 0, button_width, 20, "Z+ Offset: ", "", 0.0, 128.0, offsetData.zPos, false, true));

		this.addButtonsH(
				new GuiButton(ELEMENTS.DONEBTN.ordinal(), 0, 0, (button_width - this.paddingX) / 2, 20, I18n.format("gui.done", new Object[0])),
				new GuiButton(ELEMENTS.CANCELBTN.ordinal(), 0, 0, (button_width - this.paddingX) / 2, 20, I18n.format("gui.cancel", new Object[0]))
		);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == ELEMENTS.DONEBTN.ordinal()) {
			for (Object b : this.buttonList) {
				GuiButton btn = (GuiButton)b;
				if (btn.id == ELEMENTS.XNEGOFFSET.ordinal()) {
					this.offsetData.xNeg = ((GuiSlider)btn).getValueInt();
				} else if (btn.id == ELEMENTS.XPOSOFFSET.ordinal()) {
					this.offsetData.xPos = ((GuiSlider)btn).getValueInt();
				} else if (btn.id == ELEMENTS.YNEGOFFSET.ordinal()) {
					this.offsetData.yNeg = ((GuiSlider)btn).getValueInt();
				} else if (btn.id == ELEMENTS.YPOSOFFSET.ordinal()) {
					this.offsetData.yPos = ((GuiSlider)btn).getValueInt();
				} else if (btn.id == ELEMENTS.ZNEGOFFSET.ordinal()) {
					this.offsetData.zNeg = ((GuiSlider)btn).getValueInt();
				} else if (btn.id == ELEMENTS.ZPOSOFFSET.ordinal()) {
					this.offsetData.zPos = ((GuiSlider)btn).getValueInt();
				}
			}
			
			this.teAntiBuilder.offsets = this.offsetData;
			AntiBuilder.network.sendToServer(new AntiBuilderMsgUpdate(this.teAntiBuilder));
			this.mc.thePlayer.closeScreen();
		} else if (button.id == ELEMENTS.CANCELBTN.ordinal()) {
			this.mc.thePlayer.closeScreen();
		}
	}
	
	public void addButton(GuiButton btn) {
		btn.xPosition = offsetXcounter;
		btn.yPosition = offsetYcounter;
		this.buttonList.add(btn);
		
		this.offsetYcounter += btn.height + this.paddingY;
	}
	
	public void addButtonsH(GuiButton... btns) {
		for (GuiButton btn : btns) {
			btn.xPosition = offsetXcounter;
			btn.yPosition = offsetYcounter;
			this.buttonList.add(btn);
			this.offsetXcounter += btn.width + this.paddingX;
		}
	}
}

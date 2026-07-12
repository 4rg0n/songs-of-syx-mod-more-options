package menu;

class ScMainDisabled {
//	implements SC{
//
//	private final GuiSection first;
//	private final GuiSection play;
//	private final GuiSection load;
//	private GuiSection current;
//	private final RENDEROBJ.Sprite logo;
//	private final MenuDisabled menu;
//	private final GText version = new GText(UI.FONT().H2, VERSION.VERSION_STRING);
//	ScMainDisabled(MenuDisabled menu) {
//
//		D.t(this);
//		this.menu = menu;
//		first = getFirst(menu);
//		play = getPlay(menu);
//		play.body().moveY1(first.body().y1());
//		load = getLoad(menu);
//		load.body().moveY1(first.body().y1());
//
//		logo = new RENDEROBJ.Sprite(menu.res.s().logo);
//		logo.body().moveX2(left.x2());
//		logo.body().centerY(left);
//		logo.setColor(GUI.COLORS.menu);
//
//
//
//		current = first;
//
//	}
//
//	private static CharSequence ¤¤continue = "continue";
//	private static CharSequence ¤¤quit = "quit";
//	private static CharSequence ¤¤play = "play";
//	private static CharSequence ¤¤editor = "editor";
//	private static CharSequence ¤¤battle = "quick battle";
//	private static CharSequence ¤¤load = "load";
//	private static CharSequence ¤¤loadB = "debug battle";
//	private static CharSequence ¤¤tutorial = "tutorial";
//
//	static {
//		D.ts(ScMainDisabled.class);
//	}
//
//	private GuiSection getFirst(MenuDisabled menu){
//
//		GuiSection current = new GuiSection();
//		CLICKABLE text;
//
//		text = getNavButt(¤¤play);
//		text.clickActionSet(new ACTION() {
//			@Override
//			public void exe() {
//				switchNavigator(play);
//			}
//		});
//		current.addDown(0, text);
//
//		if (!menu.load.hasSaves()) {
//			text = new Button(UI.FONT().H1.getText(¤¤tutorial)) {
//
//				@Override
//				protected void clickA() {
//					menu.switchScreen(menu.campaigns);
//				}
//			};
//			current.addDown(8, text);
//
//		}else {
//			text = new Button(UI.FONT().H1.getText(¤¤continue)) {
//				@Override
//				protected void render(SPRITE_RENDERER r, float ds, boolean isActive, boolean isSelected,
//						boolean isHovered) {
//					activeSet(menu.load.hasSaves());
//					super.render(r, ds, isActive, isSelected, isHovered);
//				}
//
//				@Override
//				protected void clickA() {
//					if (menu.load.hasSaves())
//						menu.load.loadSave();
//				}
//			};
//			current.addDown(8, text);
//		}
//
//
//
//		text = getNavButt(ScOptions.¤¤name);
//		text.clickActionSet(new ACTION() {
//			@Override
//			public void exe() {
//				menu.switchScreen(menu.options);
//			}
//		});
//		current.addDown(8, text);
//
//		// MODDED
//		text = getNavButt("More Options (experimental)");
//		text.clickActionSet(new ACTION() {
//			@Override
//			public void exe() {
//				menu.switchScreen(menu.moreOptions);
//			}
//		});
//		current.addDown(8, text);
//
//		text = getNavButt(ScCredits.¤¤name);
//		text.clickActionSet(new ACTION() {
//			@Override
//			public void exe() {
//				menu.switchScreen(menu.credits);
//			}
//		});
//		current.addDown(8, text);
//
//		text = getNavButt(¤¤quit);
//		text.clickActionSet(new ACTION() {
//			@Override
//			public void exe() {
//				CORE.annihilate();
//			}
//		});
//		current.addDown(8, text);
//
//		current.body().moveX1(right.x1());
//		current.body().centerY(right.y1(), right.y2());
//
//		return current;
//	}
//
//
//	private GuiSection getLoad(MenuDisabled menu){
//
//		GuiSection current = new GuiSection();
//
//		CLICKABLE text;
//
//		text = getNavButt(MenuScreenLoad.¤¤name);
//		text.clickActionSet(new ACTION() {
//			@Override
//			public void exe() {
//				menu.switchScreen(menu.load);
//			}
//		});
//		current.addDown(0, text);
//
//		if (S.get().developer && PATHS.local().save().exists(BattleState.debugLoad)) {
//			text = getNavButt(¤¤loadB);
//			text.clickActionSet(new ACTION() {
//				@Override
//				public void exe() {
//					menu.start(new GameLoader(PATHS.local().save().get(BattleState.debugLoad)) {
//
//						@Override
//						public void doAfterSet() {
//							BattleState.setLoaded(new BattleStateExiter() {
//
//								@Override
//								public void afterExit(BattleStateResult res) {
//
//								}
//
//								@Override
//								public void exit(BATTLE_RESULT res, int plosses, int elosses) {
//									CORE.setCurrentState(new CORE_STATE.Constructor() {
//										@Override
//										public CORE_STATE getState() {
//											return MenuDisabled.make();
//										}
//									});
//								}
//
//							}, saveFile, true);
//						}
//
//					});
//				}
//			});
//			current.addDown(8, text);
//		}
//
//		for (ScLoad l : menu.loads) {
//			text = getNavButt(l.name);
//			text.clickActionSet(new ACTION() {
//				@Override
//				public void exe() {
//					menu.switchScreen(l);
//				}
//			});
//			if (!l.hasSaves())
//				text.activeSet(false);
//			current.addDown(8, text);
//		}
//
//		text = getBackArrow();
//		text.clickActionSet(new ACTION() {
//			@Override
//			public void exe() {
//				switchNavigator(play);
//			}
//		});
//		current.addDown(10, text);
//
//		current.body().moveX1(right.x1());
//		current.body().centerY(right);
//
//		return current;
//	}
//
//	private GuiSection getPlay(MenuDisabled menu){
//
//		GuiSection current = new GuiSection();
//
//		CLICKABLE text;
//
//		text = getNavButt(¤¤load);
//		text.clickActionSet(new ACTION() {
//			@Override
//			public void exe() {
//				switchNavigator(load);
//			}
//		});
//		current.addDown(0, text);
//
//		text = getNavButt(ScCampaign.¤¤name);
//		text.clickActionSet(new ACTION() {
//			@Override
//			public void exe() {
//				menu.switchScreen(menu.campaigns);
//
//			}
//		});
//		current.addDown(8, text);
//
//		text = getNavButt(ScRandom.¤¤name);
//		text.clickActionSet(new ACTION() {
//			@Override
//			public void exe() {
//				menu.switchScreen(menu.sandbox2);
//			}
//		});
//		current.addDown(8, text);
//
//		text = getNavButt(¤¤battle);
//		text.clickActionSet(new ACTION() {
//			@Override
//			public void exe() {
//				menu.start(new Constructor() {
//
//					@Override
//					public CORE_STATE getState() {
//						CORE_STATE s = GAME.create();
//
//						VIEW.b().editor.activate();
//
//						return s;
//					}
//				});
//			}
//		});
//		current.addDown(8, text);
//
//		text = getNavButt(¤¤editor);
//		text.clickActionSet(new ACTION() {
//			@Override
//			public void exe() {
//				menu.start(new Constructor() {
//
//					@Override
//					public CORE_STATE getState() {
//						// MODDED add forceInit() true scripts for scenario editor
//						String[] scripts = Lists.fromGameLIST(ScriptEngine.getAll()).stream()
//							.filter(scriptLoad -> scriptLoad.script.forceInit())
//							.map(scriptLoad -> scriptLoad.key)
//							.toArray(String[]::new);
//
//						CORE_STATE s = GAME.create(scripts);
//
//						VIEW.world().editor.activate();
//
//						return s;
//					}
//				});
//			}
//		});
//		current.addDown(8, text);
//
//		text = getBackArrow();
//		text.clickActionSet(new ACTION() {
//			@Override
//			public void exe() {
//				switchNavigator(first);
//			}
//		});
//		current.addDown(10, text);
//
//		current.body().moveX1(right.x1());
//		current.body().centerY(right);
//
//		return current;
//	}
//
//	private void switchNavigator(GuiSection section){
//		current = section;
//		current.hover(menu.getMCoo());
//	}
//
//	@Override
//	public void render(SPRITE_RENDERER r, float ds) {
//		logo.render(r, ds);
//		current.render(r, ds);
//		version.render(r, C.DIM().x2()-32-version.width(), 32);
//	}
//
//	@Override
//	public boolean hover(COORDINATE mCoo) {
//		return current.hover(mCoo);
//	}
//
//	@Override
//	public boolean click() {
//		return current.click();
//	}
//
//	@Override
//	public boolean back(MenuDisabled menu) {
//		if (current == load) {
//			switchNavigator(play);
//			return true;
//		}
//		if (current != first) {
//			switchNavigator(first);
//			return true;
//		}
//		return false;
//	}

}

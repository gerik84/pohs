package com.redline.shop;

import android.support.annotation.NonNull;

import com.redline.shop.Interface.Adapters.AdapterFactory;
import com.redline.shop.Interface.Fragment.FragmentCatalogPage;
import com.redline.shop.Utils.Preconditions;


public class ClassProvider {

	private static ClassProvider s_instance;

	public ClassProvider() {
//		CLASS_FRAGMENT_MAIN_MENU = FragmentMenu.class;
		CLASS_FRAGMENT_CATALOG_PAGE = FragmentCatalogPage.class;

	}

	public static ClassProvider getInstance() {

		if (s_instance == null) {
			try {
				s_instance = BuildConfig.CLASS_PROVIDER.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		Preconditions.checkNotNull(s_instance);

		return s_instance;
	}

//	public Class<? extends MainActivityClickHandler> CLASS_MAIN_ACTIVITY_CLICK_HANDLER;
	public Class<? extends FragmentCatalogPage> CLASS_FRAGMENT_CATALOG_PAGE;
	public Class<? extends AdapterFactory> CLASS_CATALOG_ADAPTER_FACTORY;
//	public Class<? extends FragmentMenu> CLASS_FRAGMENT_MAIN_MENU;
//	public Class<? extends MessagesListAdapter> CLASS_MESSAGES_ADAPTER;

//	protected Class<? extends PushProcessor> CLASS_PUSH_PROCESSOR;
//	private PushProcessor s_pushProcessor;

//	@NonNull
//	public PushProcessor getPushProcessorSingleton() {
//
//		if (s_pushProcessor == null) {
//			try {
//				s_pushProcessor = CLASS_PUSH_PROCESSOR.newInstance();
//			} catch (InstantiationException e) {
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				e.printStackTrace();
//			}
//		}
//
//		Preconditions.checkNotNull(s_pushProcessor);
//		return s_pushProcessor;
//	}

}

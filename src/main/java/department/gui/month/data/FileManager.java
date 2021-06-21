package department.gui.month.data;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.IOException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import department.gui.ErrorHandler;

public class FileManager {
	
	private static final String CACHE_DATA_MONTHS_PATH = "months_data";
	private static final String HISTORY_FOLDER_PATH = "history";
	
	/**
	 * Перемещает кэш старого месяца в папку истории
	 */
	public static void moveCurretFileToHistory() {
		File file = new File(CACHE_DATA_MONTHS_PATH);
		
		if (file.exists()) {
			createHistoryDir();
			file.renameTo(new File(HISTORY_FOLDER_PATH + "/" + System.currentTimeMillis()));
		}
	}
	
	private static void createHistoryDir() {
		File dir = new File(HISTORY_FOLDER_PATH);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}
	
	/**
	 * Десериализация данных файла кэша
	 * 
	 * @return сохранённые данные для загрузки таблиц
	 */
	public static SavedMonthsTablesData readCacheFile() {
		File file = new File(CACHE_DATA_MONTHS_PATH);
		if (!file.exists())
			return null;
		
		try (FileInputStream is = new FileInputStream(CACHE_DATA_MONTHS_PATH); 
				ObjectInputStream objectOutputStream = new ObjectInputStream(is)) {
			
			return (SavedMonthsTablesData) objectOutputStream.readObject();
		} catch (IOException | ClassNotFoundException ex) {
			ErrorHandler.show(ex);
			return null;
		}
	}
	
	/**
	 * Cериализация данных таблиц месяцев в файл
	 * 
	 * @param data данные таблиц месяцев
	 */
	public static void saveCacheFile(SavedMonthsTablesData data) {
		try (FileOutputStream os = new FileOutputStream(CACHE_DATA_MONTHS_PATH); 
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(os)) {
			
			objectOutputStream.writeObject(data);
		} catch (IOException ex) {
			ErrorHandler.show(ex);
		}
		
		System.out.println("SavedMonthsTablesData::save");
	}
}

package net.torocraft.chess.items.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class BookCreator {

	public static ItemStack createBook(String name) {
		ItemStack book = null;
		try {
			book = loadBook(name);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (book == null) {
			book = new ItemStack(Items.BOOK);
		}

		return book;
	}

	private static ItemStack loadBook(String name) throws IOException {
		String path = "/assets/torochess/books/" + name + ".txt";
		InputStream is = BookCreator.class.getResourceAsStream(path);

		if (is == null) {
			System.out.println("Book file not found [" + path + "]");
			return ItemStack.EMPTY;
		}

		ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		String line = null;
		int lineNumber = 1;
		NBTTagList pages = new NBTTagList();
		String[] words;
		StringBuilder page = new StringBuilder(256);
		try {

			while ((line = reader.readLine()) != null) {

				line = line.trim();

				if (lineNumber < 4 && line.length() == 0) {
					continue;
				}

				if (lineNumber == 1) {
					book.setTagInfo("title", new NBTTagString(line));
				} else if (lineNumber == 2) {
					book.setTagInfo("author", new NBTTagString(line));
				} else {

					if (line.length() == 0) {
						page = writePage(pages, page);
					}

					words = line.split("\\s+");

					for (String word : words) {
						if (page.length() + word.length() > 255) {
							page = writePage(pages, page);

						} else if (page.length() > 0) {
							page.append(" ");
						}

						page.append(word);
					}

				}

				lineNumber++;
			}

			page = writePage(pages, page);

		} catch (IndexOutOfBoundsException e) {
			System.out.println(e.getMessage());
		}

		book.setTagInfo("pages", pages);

		return book;
	}

	private static StringBuilder writePage(NBTTagList pages, StringBuilder page) {
		pages.appendTag(createPage(page.toString()));
		page = new StringBuilder(256);
		if (pages.tagCount() >= 50) {
			throw new IndexOutOfBoundsException("out of book pages");
		}
		return page;
	}

	private static NBTTagString createPage(String page) {
		return new NBTTagString(ITextComponent.Serializer.componentToJson(new TextComponentString(page)));
	}

}

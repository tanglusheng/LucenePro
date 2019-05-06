package com.tls.test;

import java.io.File;
import java.io.FileReader;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class HelloLucene {
	/**
	 * 创建索引
	 */
	@Test
	public void index() throws Exception {
		// 创建Directory,内存索引
		// Directory directory=new RAMDirectory();
		Directory directory = FSDirectory.open(new File("D:/github/LucenePro/src/main/resources/data/index01"));
		// 创建IndexWriter
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35));
		IndexWriter writer = null;
		writer = new IndexWriter(directory, iwc);
		// 创建Document
		Document doc = null;
		// 为Document添加Field
		File f = new File("D:/github/LucenePro/src/main/resources/data/example");
		for (File file : f.listFiles()) {
			doc = new Document();
			doc.add(new Field("content", new FileReader(file)));
			doc.add(new Field("filename", file.getName(), Store.YES, Field.Index.NOT_ANALYZED));
			doc.add(new Field("path", file.getAbsolutePath(), Store.YES, Field.Index.NOT_ANALYZED));
			// 把文档添加到索引中
			writer.addDocument(doc);
		}
		writer.close();
	}

	/**
	 * 搜索
	 * 
	 * @throws Exception
	 */
	@Test
	public void search() throws Exception {
		// 创建Direction
		Directory directory = FSDirectory.open(new File("D:/github/LucenePro/src/main/resources/data/index01"));
		// 创建IndexReader
		IndexReader reader = IndexReader.open(directory);
		// 根据IndexReader创建IndexSearcher
		IndexSearcher searcher = new IndexSearcher(reader);
		// 创建搜索的Query
		QueryParser parser = new QueryParser(Version.LUCENE_35, "content", new StandardAnalyzer(Version.LUCENE_35));
		Query query = parser.parse("Character");
		// 根据searcher搜索并返回TopDocs
		TopDocs tds = searcher.search(query, 10);
		// 根据TopDocs获取ScoreDoc对象
		ScoreDoc[] sds = tds.scoreDocs;
		for (ScoreDoc sd : sds) {
			// 根据searcher和ScoreDoc对象获取具体的Document对象
			Document d = searcher.doc(sd.doc);
			// 根据Document对象获取需要的值
			System.out.println(d.get("filename") + "----" + d.get("path"));
		}
		// 关闭reader
		searcher.close();
		reader.close();
	}
}

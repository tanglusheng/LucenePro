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
	 * ��������
	 */
	@Test
	public void index() throws Exception {
		// ����Directory,�ڴ�����
		// Directory directory=new RAMDirectory();
		Directory directory = FSDirectory.open(new File("D:/github/LucenePro/src/main/resources/data/index01"));
		// ����IndexWriter
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35));
		IndexWriter writer = null;
		writer = new IndexWriter(directory, iwc);
		// ����Document
		Document doc = null;
		// ΪDocument���Field
		File f = new File("D:/github/LucenePro/src/main/resources/data/example");
		for (File file : f.listFiles()) {
			doc = new Document();
			doc.add(new Field("content", new FileReader(file)));
			doc.add(new Field("filename", file.getName(), Store.YES, Field.Index.NOT_ANALYZED));
			doc.add(new Field("path", file.getAbsolutePath(), Store.YES, Field.Index.NOT_ANALYZED));
			// ���ĵ���ӵ�������
			writer.addDocument(doc);
		}
		writer.close();
	}

	/**
	 * ����
	 * 
	 * @throws Exception
	 */
	@Test
	public void search() throws Exception {
		// ����Direction
		Directory directory = FSDirectory.open(new File("D:/github/LucenePro/src/main/resources/data/index01"));
		// ����IndexReader
		IndexReader reader = IndexReader.open(directory);
		// ����IndexReader����IndexSearcher
		IndexSearcher searcher = new IndexSearcher(reader);
		// ����������Query
		QueryParser parser = new QueryParser(Version.LUCENE_35, "content", new StandardAnalyzer(Version.LUCENE_35));
		Query query = parser.parse("Character");
		// ����searcher����������TopDocs
		TopDocs tds = searcher.search(query, 10);
		// ����TopDocs��ȡScoreDoc����
		ScoreDoc[] sds = tds.scoreDocs;
		for (ScoreDoc sd : sds) {
			// ����searcher��ScoreDoc�����ȡ�����Document����
			Document d = searcher.doc(sd.doc);
			// ����Document�����ȡ��Ҫ��ֵ
			System.out.println(d.get("filename") + "----" + d.get("path"));
		}
		// �ر�reader
		searcher.close();
		reader.close();
	}
}

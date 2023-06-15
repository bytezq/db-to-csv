package com.example.demo;

import com.example.demo.service.CsvExporter;
import com.example.demo.service.DBService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

	@Resource
	private DBService dbService;
	@Resource
	private CsvExporter csvExporter;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		List<String> tableNames = dbService.queryTableNames();
		displayTableNames(tableNames);

		Scanner scanner = new Scanner(System.in);
		while (true) {
			String input = scanner.nextLine();
			if ("exit".equalsIgnoreCase(input)) {
				break;
			}
			int index;
			try {
				index = Integer.parseInt(input);
				if (index < 1 || index > tableNames.size()) {
					throw new IllegalArgumentException();
				}
			} catch (Exception e) {
				System.out.print("输入错误，请输入正确的序号：");
				continue;
			}

			String tableName = tableNames.get(index - 1);
			System.out.println("您选择的表名称是：" + tableName);
			csvExporter.exportTableToCsv(tableName);
			System.out.print("请输入序号：");
		}
		System.out.println("已退出选择。");
	}

	private void displayTableNames(List<String> tableNames) {
		System.out.println("数据库中的表名称如下：");
		for (int i = 0; i < tableNames.size(); i++) {
			System.out.printf("%d. %s\n", i + 1, tableNames.get(i));
		}
		System.out.print("请输入序号以选择表名称：");
	}
}

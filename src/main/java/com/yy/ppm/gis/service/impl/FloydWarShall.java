package com.yy.ppm.gis.service.impl;

import java.util.List;

public class FloydWarShall {
	/**
	 * 保存边的权重、距离*/
	private double[][] d;
	/**
	 * 保存所有的边*/
	public int[][] prev;
	/**
	 * 顶点的个数*/
	private int v;

	private boolean negativeCycle;

	private String shortWay;

	public FloydWarShall(int v) {
		this.v = v;

		d = new double[v][v];

		prev = new int[v][v];

		// 默认设置所有节点都不可达，而自己到自己是可达并且距离为0.0
		for (int i = 0; i < v; i++) {
			for (int j = 0; j < v; j++) {
				d[i][j] = 999999.0;
				prev[i][j] = -1;
				if (i == j) {
					d[i][j] = 0;
				}
			}
		}
	}

	/**
	 *
	 * @Title: findShortestPath
	 * @Description: 查询最短路径
	 * @return void 返回类型
	 * @throws
	 */
	public void findShortestPath(List<Integer> pointIdList) {
		// 查找最短路径
		for (int k = 0; k < v; k++) {
			if(!pointIdList.contains(k)){
				continue;
			}
			// 将每个k值考虑成i|j路径中的一个中间点
			for (int i = 0; i < v; i++) {
				for (int j = 0; j < v; j++) {
					// 如果存在使得权重和更小的中间值k，就更新最短路径为经过k的路径
					if (d[i][j] > d[i][k] + d[k][j]) {
						d[i][j] = d[i][k] + d[k][j];
						prev[i][j] = k;
					}
				}
			}
		}

//		// 四舍五入距离
//		for (int i = 0; i < v; i++) {
//			for (int j = 0; j < v; j++) {
//				d[i][j] = new BigDecimal(d[i][j]).setScale(2,
//						RoundingMode.HALF_UP).doubleValue();
//			}
//		}

		// 检测负权重环的方式很简单，就是判断所有i->i的距离d[i][i]，如果存在小于0的，表示这个i->i的环路的权重和形成了一个负值，也就是存在这个负权重
		// 在之前的其他最短路径算法中，无法通过这个方法来检测负环，因为之前路径距离都是保存在一个一维数组中，相等于只能检测d[0][0]，无法检测每个d[i][i]
		for (int i = 0; i < v; i++) {
			if (d[i][i] < 0){
                negativeCycle = true;
            }
		}
	}

	/**
	 *
	 * @Title: hasNegativeCycle
	 * @Description: 是否拥有负权重环
	 * @param
	 * @return 设定文件
	 * @return boolean 返回类型
	 * @throws
	 */
	public boolean hasNegativeCycle() {
		return negativeCycle;
	}

	/**
	 *
	 * @Title: distTo
	 * @Description: a->b最短路径的距离
	 * @param
	 * @param a
	 * @param
	 * @param b
	 * @param
	 * @return 设定文件
	 * @return double 返回类型
	 * @throws
	 */
	public double distTo(int a, int b) {
		if (hasNegativeCycle()){
            throw new RuntimeException("有负权重环，不存在最短路径");
        }
		return d[a][b];
	}

	/**
	 *
	 * @Title: printShortestPath
	 * @Description: 打印a->b最短路径
	 * @param
	 * @return 设定文件
	 * @return Iterable<Integer> 返回类型
	 * @throws
	 */
	public String getShortestPath(int a, int b) {
		shortWay = "";
		if (hasNegativeCycle()) {
			shortWay = "有负权重环，不存在最短路径";
		} else if (a == b){
            shortWay =a + "|" + b;
        }
		else {
			shortWay += a + "|";
			path(a, b);
			shortWay +=b;
		}
		return shortWay;
	}

	private void path(int a, int b) {
		int _k = prev[a][b];

		if (_k == -1) {
			return;
		}else{
			path(a, _k);
			shortWay +=_k + "|";
			path(_k, b);
		}
	}

	/**
	 *
	 * @Title: addEdge
	 * @Description: 添加边
	 * @param
	 * @param a
	 * @param
	 * @param b
	 * @param
	 * @param w
	 *            设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void addEdge(int a, int b, double w,int lxlx) {
		d[a][b] = w;
		if(lxlx==1){
			return;
		}
		d[b][a] = w;
	}

	/**
	 *
	 * @Title: addEdge
	 * @Description: 删除边 实际就是把对应的权重设为无穷大 保证从该路线走的不是最优路径
	 * @param
	 * @param a
	 * @param
	 * @param b
	 * @param
	 * @return void 返回类型
	 * @throws
	 */
	public void removeEdge(int a, int b) {
		d[b][a] = 999999;
	}
}

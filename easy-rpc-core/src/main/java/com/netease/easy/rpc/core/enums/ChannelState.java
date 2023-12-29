package com.netease.easy.rpc.core.enums;

public enum ChannelState {
	/**
	 * 未初始化状态
	 */
	UN_INIT(0),

	/**
	 * 初始化完成
	 */
	INIT(1),

	/**
	 * 可用状态
	 */
	ALIVE(2),

	/**
	 * 不可用状态
	 */
	UN_ALIVE(3),

	/**
	 * 关闭状态
	 */
	CLOSE(4);

	public final int value;

	private ChannelState(int value) {
		this.value = value;
	}

	public boolean isAliveState() {
		return this == ALIVE;
	}
	
	public boolean isUnAliveState() {
		return this == UN_ALIVE;
	}

	public boolean isCloseState() {
		return this == CLOSE;
	}

	public boolean isInitState() {
		return this == INIT;
	}
	
	public boolean isUnInitState() {
		return this == UN_INIT;
	}
}

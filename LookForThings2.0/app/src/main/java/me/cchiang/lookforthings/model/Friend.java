package me.cchiang.lookforthings.model;

import java.io.Serializable;

import me.cchiang.lookforthings.User;

public class Friend implements Serializable {
	private long id;
	private String uid;
	private String name;
	private int photo;
	private String photoUrl;

	public Friend(long id, String name, int photo) {
		this.id = id;
		this.name = name;
		this.photo = photo;
	}

	public Friend(String name, int photo) {
		this.name = name;
		this.photo = photo;
	}

	public Friend(User user){
		this.uid =  user.getUid();
		this.name = user.getDisplayName();
		if(user.getPhotoUrl() != null){
			this.photoUrl = user.getPhotoUrl();
		}
		this.photo = 1;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getPhoto() {
		return photo;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
}

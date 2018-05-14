package com.gildedgames.orbis_api.world.data;

import org.lmdbjava.Dbi;
import org.lmdbjava.DbiFlags;
import org.lmdbjava.Env;
import org.lmdbjava.Txn;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;

public class WorldDataManagerLmdb implements IWorldDataManager
{
	private final File file;

	private final Env<ByteBuffer> env;

	private final HashMap<String, Dbi<ByteBuffer>> registeredDatabases = new HashMap<>();
	private final HashMap<String, IWorldData> registeredDatas = new HashMap<>();

	private Txn<ByteBuffer> write;

	public WorldDataManagerLmdb(File file)
	{
		this.file = new File(file, "lmdb");

		if (!this.file.isDirectory() && !this.file.mkdirs())
		{
			throw new RuntimeException("Failed to create data directory at " + this.file.getAbsolutePath());
		}

		this.env = Env.create().setMapSize(1024L * 1024L * 1024L).setMaxDbs(128).setMaxReaders(128).open(this.file);
	}

	@Override
	public void register(IWorldData data)
	{
		if (data.getName() == null)
		{
			throw new IllegalArgumentException("Data name can not be null");
		}

		this.registeredDatabases.put(data.getName().toString(), env.openDbi(data.getName().toString(), DbiFlags.MDB_CREATE));
		this.registeredDatas.put(data.getName().toString(), data);
	}

	@Override
	public byte[] readBytes(IWorldData data, String path)
	{
		IWorldData item = this.registeredDatas.get(data.getName().toString());
		Dbi<ByteBuffer> db = this.registeredDatabases.get(data.getName().toString());

		if (item == null)
		{
			throw new IllegalArgumentException("Data not registered");
		}

		if (db == null)
		{
			throw new IllegalArgumentException("Database not open");
		}

		ByteBuffer key = this.createKey(path);

		try (Txn<ByteBuffer> txn = this.env.txnRead())
		{
			final ByteBuffer found = db.get(txn, key);

			if (found == null)
			{
				return null;
			}

			byte[] bytes = new byte[found.capacity()];
			found.get(bytes);

			return bytes;
		}
	}

	@Override
	public void writeBytes(IWorldData data, String path, byte[] bytes)
	{
		if (this.write == null)
		{
			throw new IllegalStateException("Write Txn not available (did you wait for flush()?)");
		}

		IWorldData item = this.registeredDatas.get(data.getName().toString());
		Dbi<ByteBuffer> db = this.registeredDatabases.get(data.getName().toString());

		if (item == null)
		{
			throw new IllegalArgumentException("Data not registered");
		}

		if (db == null)
		{
			throw new IllegalArgumentException("Database not open");
		}

		ByteBuffer key = this.createKey(path);

		ByteBuffer val = ByteBuffer.allocateDirect(bytes.length);
		val.put(bytes);
		val.flip();

		db.put(this.write, key, val);
	}

	@Override
	public void flush()
	{
		try (Txn<ByteBuffer> txn = this.env.txnWrite())
		{
			this.write = txn;

			this.registeredDatas.values().forEach(IWorldData::flush);

			txn.commit();
		}
		finally
		{
			this.write = null;
		}
	}

	private ByteBuffer createKey(String path)
	{
		final byte[] keyBytes = path.getBytes(Charset.forName("UTF-8"));

		if (keyBytes.length > this.env.getMaxKeySize())
		{
			throw new IllegalArgumentException("Key length is too long (len: " + keyBytes.length + ", max: " + this.env.getMaxKeySize() + ")");
		}

		final ByteBuffer key = ByteBuffer.allocateDirect(keyBytes.length);
		key.put(keyBytes).flip();

		return key;
	}

}

package com.gildedgames.orbis_api.world.data;

import com.gildedgames.orbis_api.OrbisAPI;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WorldDataManagerFlat implements IWorldDataManager
{
	private final File file;

	private final List<IWorldData> registered = new ArrayList<>();

	private boolean closed;

	public WorldDataManagerFlat(File file)
	{
		this.file = new File(file, "flat");

		OrbisAPI.LOGGER.info("Creating file-backed world storage at " + this.file.getAbsolutePath());
	}

	@Override
	public void register(IWorldData data)
	{
		this.checkClosed();

		if (data.getName() == null)
		{
			throw new IllegalArgumentException("Data name can not be null");
		}

		File dirs = new File(this.file, data.getName().getNamespace() + "/" + data.getName().getPath());

		if (!dirs.isDirectory() && !dirs.mkdirs())
		{
			throw new RuntimeException("Failed to create data directory at " + dirs.getAbsolutePath());
		}

		this.registered.add(data);
	}

	@Override
	public byte[] readBytes(IWorldData data, String path) throws IOException
	{
		this.checkClosed();

		File file = this.getFile(data, path);

		if (!file.exists())
		{
			return null;
		}

		try (ByteArrayOutputStream out = new ByteArrayOutputStream())
		{
			try (FileInputStream in = new FileInputStream(file))
			{
				IOUtils.copy(in, out);
			}

			out.flush();

			return out.toByteArray();
		}
	}

	@Override
	public void writeBytes(IWorldData data, String path, byte[] bytes) throws IOException
	{
		this.checkClosed();

		File file = this.getFile(data, path);
		File parent = file.getParentFile();

		if (!parent.isDirectory() && !parent.mkdirs())
		{
			throw new RuntimeException("Failed to create parent directory for " + file.getAbsolutePath());
		}

		try (FileOutputStream out = new FileOutputStream(file))
		{
			IOUtils.write(bytes, out);
		}
	}

	private File getFile(IWorldData data, String path)
	{
		return new File(this.file, data.getName().getNamespace() + "/" + data.getName().getPath() + "/" + path);
	}

	@Override
	public void flush()
	{
		this.registered.forEach(IWorldData::flush);
	}

	@Override
	public void close()
	{
		this.flush();
	}

	private void checkClosed()
	{
		if (this.closed)
		{
			throw new IllegalStateException("World storage has been closed!");
		}
	}
}

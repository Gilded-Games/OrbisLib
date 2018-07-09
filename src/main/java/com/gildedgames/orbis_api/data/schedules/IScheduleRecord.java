package com.gildedgames.orbis_api.data.schedules;

import com.gildedgames.orbis_api.util.mc.NBT;
import com.gildedgames.orbis_api.world.IWorldObjectChild;

import java.util.List;

/**
 * An object to hold ISchedules. Used by IScheduleLayers.
 */
public interface IScheduleRecord extends NBT, IWorldObjectChild
{

	void listen(IScheduleRecordListener listener);

	boolean unlisten(IScheduleRecordListener listener);

	void removeSchedule(int id);

	/**
	 * Should check if the schedule fits within the record
	 * @param schedule
	 * @return -1 if the schedule did not setUsedData
	 */
	int addSchedule(final ISchedule schedule);

	/**
	 *
	 * @param id
	 * @param schedule
	 * @return Whether the schedule successfully setUsedData or not
	 */
	boolean setSchedule(int id, ISchedule schedule);

	/**
	 * @param schedule
	 * @return Returns the id for this schedule.
	 * Returns -1 if there is no schedule present in this record.
	 */
	int getScheduleId(final ISchedule schedule);

	<T extends ISchedule> T getSchedule(int id);

	<T extends ISchedule> List<T> getSchedules(Class<T> clazz);

	List<ISchedule> getSchedulesFromTriggerID(String triggerId);

	IScheduleLayer getParent();

	void setParent(IScheduleLayer parent);

}

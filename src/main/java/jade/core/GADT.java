/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
 *****************************************************************/

package jade.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The table holding information about all agents known to the
 * platform (both agents living in the platform and remote agents
 * registered with the platform AMS).
 * @author Giovanni Caire - TILAB
 * @author Giovanni Rimassa - Universita` di Parma
 * @author Moreno LAGO
 */
public class GADT {

    private final Map<AID, Row> agents = new ConcurrentHashMap<>();

    /**
     * Adds or updates an agent descriptor in the GADT.
     * @param aid The AID of the agent.
     * @param agentDescriptor The agent descriptor to add or update.
     * @return The old agent descriptor if it was replaced, otherwise null.
     */
    public AgentDescriptor put(AID aid, AgentDescriptor agentDescriptor) {
        Row row = agents.get(aid);
        if (row == null) {
            Row newRow = new Row(agentDescriptor);
            Row existingRow = agents.putIfAbsent(aid, newRow);
            return existingRow == null ? null : existingRow.get();
        } else {
            row.lock();
            try {
                AgentDescriptor oldDescriptor = row.get();
                row.set(agentDescriptor);
                return oldDescriptor;
            } finally {
                row.unlock();
            }
        }
    }

    /**
     * Removes an agent descriptor from the GADT.
     * @param aid The AID of the agent to remove.
     * @return The removed agent descriptor, or null if no agent was found.
     */
    public AgentDescriptor remove(AID aid) {
        Row row = agents.get(aid);
        if (row == null) {
            return null;
        } else {
            row.lock();
            try {
                AgentDescriptor descriptor = row.get();
                row.clear();
                agents.remove(aid);
                return descriptor;
            } finally {
                row.unlock();
            }
        }
    }

    /**
     * Acquires an agent descriptor from the GADT.
     * The caller must call release() after it has finished with the descriptor.
     * @param aid The AID of the agent to acquire.
     * @return The agent descriptor, or null if no agent was found.
     */
    public AgentDescriptor acquire(AID aid) {
        Row row = agents.get(aid);
        if (row == null) {
            return null;
        } else {
            row.lock();
            return row.get();
        }
    }

    /**
     * Releases a previously acquired agent descriptor.
     * @param aid The AID of the agent to release.
     */
    public void release(AID aid) {
        Row row = agents.get(aid);
        if (row != null) {
            row.unlock();
        }
    }

    /**
     * Returns an array of all AIDs in the GADT.
     * @return An array of AIDs.
     */
    public AID[] keys() {
        return agents.keySet().toArray(new AID[0]);
    }

    /**
     * Returns an array of all agent descriptors in the GADT.
     * @return An array of agent descriptors.
     */
    public AgentDescriptor[] values() {
        return agents.values().stream()
                .map(Row::get)
                .toArray(AgentDescriptor[]::new);
    }

    /**
     * Inner class Row.
     * Rows of the GADT are protected by a reentrant lock.
     */
    private static class Row {
        private AgentDescriptor value;
        private final ReentrantLock lock = new ReentrantLock();

        public Row(AgentDescriptor value) {
            this.value = value;
        }

        public AgentDescriptor get() {
            lock.lock();
            try {
                return value;
            } finally {
                lock.unlock();
            }
        }

        public void set(AgentDescriptor value) {
            lock.lock();
            try {
                this.value = value;
            } finally {
                lock.unlock();
            }
        }

        public void clear() {
            lock.lock();
            try {
                value = null;
            } finally {
                lock.unlock();
            }
        }

        public void lock() {
            lock.lock();
        }

        public void unlock() {
            lock.unlock();
        }

        @Override
        public String toString() {
            return "Row [value=" + value + ", lock=" + lock + "]";
        }
    } // End of Row inner class
}
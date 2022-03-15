package com.loveoyh.common.entity;

public class IpRange {
    private boolean rangOrSingle = false;
    private long start;
    private long end;

    public boolean contain(long currentIp)
    {
        if (isRangOrSingle()) {
            if ((currentIp >= getStart()) && (currentIp <= getEnd())) {
                return true;
            }
        }
        else if (getStart() == currentIp) {
            return true;
        }

        return false;
    }

    public IpRange(long start)
    {
        this.start = start;
    }

    public IpRange(long start, long end)
    {
        this.start = start;
        this.end = end;
        this.rangOrSingle = true;
    }

    public boolean isRangOrSingle()
    {
        return this.rangOrSingle;
    }

    public void setRangOrSingle(boolean rangOrSingle)
    {
        this.rangOrSingle = rangOrSingle;
    }

    public long getStart()
    {
        return this.start;
    }

    public void setStart(long start)
    {
        this.start = start;
    }

    public long getEnd()
    {
        return this.end;
    }

    public void setEnd(long end)
    {
        this.end = end;
    }

    public String toString()
    {
        return "IpRange [rangOrSingle=" + this.rangOrSingle + ", start=" + this.start + ", end=" + this.end + "]";
    }
}
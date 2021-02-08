package me.skiincraft.ousubot.core.commands.options;

import me.skiincraft.api.osu.object.beatmap.Approval;

public class ApprovalOption extends CommandOption {

    private Approval approval;

    public ApprovalOption(Approval approval) {
        super(approval.name(), new String[0], "approval");
        this.approval = approval;
    }

    public Approval getApproval() {
        return approval;
    }

    public static ApprovalOption[] getScoreableOptions(){
        return new ApprovalOption[]{
                new ApprovalOption(Approval.Ranked),
                new ApprovalOption(Approval.Approved),
                new ApprovalOption(Approval.Loved),
                new ApprovalOption(Approval.Qualified),
        };
    }
}

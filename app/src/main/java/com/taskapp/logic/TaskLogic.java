package com.taskapp.logic;

import java.time.LocalDate;
import java.util.List;

import com.taskapp.dataaccess.LogDataAccess;
import com.taskapp.dataaccess.TaskDataAccess;
import com.taskapp.dataaccess.UserDataAccess;
import com.taskapp.model.User;
import com.taskapp.model.Log;
import com.taskapp.model.Task;
import com.taskapp.exception.AppException;

public class TaskLogic {
    private final TaskDataAccess taskDataAccess;
    private final LogDataAccess logDataAccess;
    private final UserDataAccess userDataAccess;


    public TaskLogic() {
        taskDataAccess = new TaskDataAccess();
        logDataAccess = new LogDataAccess();
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param taskDataAccess
     * @param logDataAccess
     * @param userDataAccess
     */
    public TaskLogic(TaskDataAccess taskDataAccess, LogDataAccess logDataAccess, UserDataAccess userDataAccess) {
        this.taskDataAccess = taskDataAccess;
        this.logDataAccess = logDataAccess;
        this.userDataAccess = userDataAccess;
    }

    /**
     * 全てのタスクを表示します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findAll()
     * @param loginUser ログインユーザー
     */
    public void showAll(User loginUser) {
        List<Task> taskList = taskDataAccess.findAll();

        for (Task task : taskList) {
            System.out.print(task.getCode() + ". タスク名：" + task.getName() + ", 担当者名：");

            // 担当者がログインしているユーザーである場合
            if (loginUser.getCode() == task.getRepUser().getCode()) {
                System.out.print("あなたが担当しています, ");
            }
            // ログインしているユーザー以外の担当の場合
            else {
                System.out.print(task.getRepUser().getName() + "が担当しています, ");
            }

            switch (task.getStatus()) {
                case 0:
                    System.out.println("ステータス：未着手");
                    break;
                case 1:
                    System.out.println("ステータス：着手中");
                    break;
                case 2:
                    System.out.println("ステータス：完了");
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 新しいタスクを保存します。
     *
     * @see com.taskapp.dataaccess.UserDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#save(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code タスクコード
     * @param name タスク名
     * @param repUserCode 担当ユーザーコード
     * @param loginUser ログインユーザー
     * @throws AppException ユーザーコードが存在しない場合にスローされます
     */
    public void save(int code, String name, int repUserCode, User loginUser) throws AppException {
        // ユーザーコードが存在しない場合、エラーを返す
        if (userDataAccess.findByCode(repUserCode) == null) {
            throw new AppException("存在するユーザーコードを入力してください");
        }

        // タスクとログの情報を追加
        taskDataAccess.save(new Task(code, name, 0, userDataAccess.findByCode(repUserCode)));
        logDataAccess.save(new Log(code, repUserCode, 0, LocalDate.now()));
    }

    /**
     * タスクのステータスを変更します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#update(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code タスクコード
     * @param status 新しいステータス
     * @param loginUser ログインユーザー
     * @throws AppException タスクコードが存在しない、またはステータスが前のステータスより1つ先でない場合にスローされます
     */
    public void changeStatus(int code, int status, User loginUser) throws AppException {
        Task task = taskDataAccess.findByCode(code);

        if (taskDataAccess.findByCode(code) == null) {
            throw new AppException("存在するタスクコードを入力してください");
        }
        if (task.getStatus() == 0 && status == 1) {
            task.setStatus(status);
        }
        else if (task.getStatus() == 1 && status == 2) {
            task.setStatus(status);
        }
        else {
            throw new AppException("ステータスは、前のステータスより1つ先のもののみを選択してください");
        }
        taskDataAccess.update(task);
        logDataAccess.save(new Log(code, loginUser.getCode(), status, LocalDate.now()));
    }

    /**
     * タスクを削除します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#delete(int)
     * @see com.taskapp.dataaccess.LogDataAccess#deleteByTaskCode(int)
     * @param code タスクコード
     * @throws AppException タスクコードが存在しない、またはタスクのステータスが完了でない場合にスローされます
     */
    // public void delete(int code) throws AppException {
    // }
}
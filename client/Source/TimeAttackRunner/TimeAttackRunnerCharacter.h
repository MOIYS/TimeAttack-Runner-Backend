// Copyright Epic Games, Inc. All Rights Reserved.

#pragma once

#include "CoreMinimal.h"

#include "GameFramework/Character.h"

#include "Logging/LogMacros.h"
#include "Http.h"

#include "TimeAttackRunnerCharacter.generated.h"

class USpringArmComponent;
class UCameraComponent;
class UInputMappingContext;
class UInputAction;
struct FInputActionValue;

DECLARE_LOG_CATEGORY_EXTERN(LogTemplateCharacter, Log, All);

UCLASS(config=Game)
class ATimeAttackRunnerCharacter : public ACharacter
{
	GENERATED_BODY()

	UPROPERTY(VisibleAnywhere, BlueprintReadOnly, Category = Camera, meta = (AllowPrivateAccess = "true"))
	USpringArmComponent* CameraBoom;

	UPROPERTY(VisibleAnywhere, BlueprintReadOnly, Category = Camera, meta = (AllowPrivateAccess = "true"))
	UCameraComponent* FollowCamera;
	
	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = Input, meta = (AllowPrivateAccess = "true"))
	UInputMappingContext* DefaultMappingContext;

	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = Input, meta = (AllowPrivateAccess = "true"))
	UInputAction* JumpAction;

	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = Input, meta = (AllowPrivateAccess = "true"))
	UInputAction* MoveAction;

	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = Input, meta = (AllowPrivateAccess = "true"))
	UInputAction* LookAction;

public:
	ATimeAttackRunnerCharacter();

protected:
	void Move(const FInputActionValue& Value);

	void Look(const FInputActionValue& Value);
			
protected:
	virtual void SetupPlayerInputComponent(class UInputComponent* PlayerInputComponent) override;
	
	virtual void BeginPlay();

public:
	FORCEINLINE class USpringArmComponent* GetCameraBoom() const { return CameraBoom; }

	FORCEINLINE class UCameraComponent* GetFollowCamera() const { return FollowCamera; }

public:
	UPROPERTY(VisibleAnywhere, BlueprintReadOnly, Category = "Record")
    float ElapsedTime;

    UPROPERTY(EditAnywhere, BlueprintReadWrite, Category = "Record")
    FString PlayerName = TEXT("MOIYS");

	UPROPERTY(VisibleAnywhere, BlueprintReadOnly, Category = "Record")
    TArray<FVector> Locations;

	FTimerHandle RecordingTimerHandle;
	
	float RecordingStartTime =0.0f;

	UPROPERTY(Config, Editanywhere, BlueprintReadWrite, Category = "Network")
	FString ApiUrl;

    UFUNCTION(BlueprintCallable, Category = "Record")
    void StartRecording();

	UFUNCTION(BlueprintCallable, Category = "Record")
	void StopRecording();

    UFUNCTION(BlueprintCallable, Category = "Record")
    void SendRecordToServer();

private:
    void OnRecordTimeTick();

    void OnResponseReceived(FHttpRequestPtr Request, FHttpResponsePtr Response, bool bSucceeded);
};

